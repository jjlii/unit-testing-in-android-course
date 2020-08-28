package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.common.time.TimeProvider;
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest {

    private static final String QUESTION_ID_1 = "questionId_1";
    private static final String QUESTION_ID_2 = "questionId_2";
    private static final long CACHE_TIMEOUT = 60000;
    private static final String TITLE_1 = "title_1";
    private static final String TITLE_2 = "title_2";
    private static final String BODY_1 = "body_1";
    private static final String BODY_2 = "body_2";
    private QuestionSchema questionSchema1= new QuestionSchema(TITLE_1,QUESTION_ID_1,BODY_1);
    private QuestionSchema questionSchema2= new QuestionSchema(TITLE_2,QUESTION_ID_2,BODY_2);
    private QuestionDetails questionDetails1 = new QuestionDetails(QUESTION_ID_1,TITLE_1,BODY_1);
    private QuestionDetails questionDetails2 = new QuestionDetails(QUESTION_ID_2,TITLE_2,BODY_2);
    private FetchQuestionDetailsUseCase SUT;

    @Captor
    ArgumentCaptor<String> mStringCaptor;
    @Captor
    ArgumentCaptor<QuestionDetails> mQuestionDetailsCaptor;

    @Mock
    FetchQuestionDetailsEndpoint mFetchQuestionDetailsEndpointMock;
    @Mock
    TimeProvider mTimeProvider;
    @Mock
    FetchQuestionDetailsUseCase.Listener mListener1;
    @Mock
    FetchQuestionDetailsUseCase.Listener mListener2;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchQuestionDetailsUseCase(mFetchQuestionDetailsEndpointMock, mTimeProvider);
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);
        success();
    }

    @Test
    public void fetchQuestionDetail_success_correctQuestionIdPassedToEndpoint() {
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);

        verify(mFetchQuestionDetailsEndpointMock).fetchQuestionDetails(mStringCaptor.capture(), any(FetchQuestionDetailsEndpoint.Listener.class));
        assertThat(mStringCaptor.getValue(), is(QUESTION_ID_1));
    }

    @Test
    public void fetchQuestionDetail_success_observerNotifiedWithCorrectResult() {
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);

        verify(mListener1).onQuestionDetailsFetched(mQuestionDetailsCaptor.capture());
        verify(mListener2).onQuestionDetailsFetched(mQuestionDetailsCaptor.capture());
        checkQuestionDetails(mQuestionDetailsCaptor.getAllValues(), questionDetails1);
    }

    @Test
    public void fetchQuestionDetail_success_unsubscribedObserversNotNotified() {
        SUT.unregisterListener(mListener1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2);

        verifyNoMoreInteractions(mListener1);
        verify(mListener2).onQuestionDetailsFetched(mQuestionDetailsCaptor.capture());
        assertThat(mQuestionDetailsCaptor.getValue(), is(questionDetails2));
    }

    @Test
    public void fetchQuestionDetail_failure_observerNotifiedWithFailureResult() {
        failure();
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);

        verify(mListener1).onQuestionDetailsFetchFailed();
        verify(mListener2).onQuestionDetailsFetchFailed();
    }

    @Test
    public void fetchQuestionDetail_secondTimeImmediatelyAfterSuccess_listenersNotifiedWithDataFromCache() {

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);

        verify(mListener1, times(2)).onQuestionDetailsFetched(mQuestionDetailsCaptor.capture());
        verify(mListener2, times(2)).onQuestionDetailsFetched(mQuestionDetailsCaptor.capture());
        checkQuestionDetails(mQuestionDetailsCaptor.getAllValues(), questionDetails1);
        verify(mFetchQuestionDetailsEndpointMock,times(1))
                .fetchQuestionDetails(anyString(), any(FetchQuestionDetailsEndpoint.Listener.class));
    }

    @Test
    public void fetchQuestionDetail_secondTimeBeforeTimeOutAfterSuccess_listenersNotifiedWithDataFromCache() {

        setTimeOut(0L);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);

        setTimeOut(CACHE_TIMEOUT - 1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);

        verify(mListener1, times(2)).onQuestionDetailsFetched(mQuestionDetailsCaptor.capture());
        verify(mListener2, times(2)).onQuestionDetailsFetched(mQuestionDetailsCaptor.capture());
        checkQuestionDetails(mQuestionDetailsCaptor.getAllValues(), questionDetails1);
        verify(mFetchQuestionDetailsEndpointMock,times(1))
                .fetchQuestionDetails(anyString(), any(FetchQuestionDetailsEndpoint.Listener.class));
    }

    @Test
    public void fetchQuestionDetails_secondTimeAfterTimeOutAfterSuccess_listenerNotifiedWithDataNotFromCache() {
        setTimeOut(0L);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);

        setTimeOut(CACHE_TIMEOUT);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);

        verify(mListener1, times(2)).onQuestionDetailsFetched(mQuestionDetailsCaptor.capture());
        verify(mListener2, times(2)).onQuestionDetailsFetched(mQuestionDetailsCaptor.capture());
        checkQuestionDetails(mQuestionDetailsCaptor.getAllValues(), questionDetails1);
        verify(mFetchQuestionDetailsEndpointMock,times(2))
                .fetchQuestionDetails(anyString(), any(FetchQuestionDetailsEndpoint.Listener.class));

    }

    @Test
    public void fetchQuestionDetail_secondTimeWithDifferentQuestionIdAfterSuccess_listenersNotifiedWithDataFromCache() {

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2);

        verify(mListener1, times(2)).onQuestionDetailsFetched(mQuestionDetailsCaptor.capture());
        verify(mListener2, times(2)).onQuestionDetailsFetched(mQuestionDetailsCaptor.capture());
        assertThat(mQuestionDetailsCaptor.getAllValues().contains(questionDetails1), is(true));
        assertThat(mQuestionDetailsCaptor.getAllValues().contains(questionDetails2), is(true));
        verify(mFetchQuestionDetailsEndpointMock,times(2))
                .fetchQuestionDetails(anyString(), any(FetchQuestionDetailsEndpoint.Listener.class));
    }

    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String questionId = (String) args[0];
                FetchQuestionDetailsEndpoint.Listener listener = (FetchQuestionDetailsEndpoint.Listener) args[1];
                if (questionId.equals(QUESTION_ID_1)){
                    listener.onQuestionDetailsFetched(questionSchema1);
                }else {
                    listener.onQuestionDetailsFetched(questionSchema2);
                }
                return null;
            }
        }).when(mFetchQuestionDetailsEndpointMock).fetchQuestionDetails(anyString(), any(FetchQuestionDetailsEndpoint.Listener.class));
    }

    private void failure() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                FetchQuestionDetailsEndpoint.Listener listener = (FetchQuestionDetailsEndpoint.Listener) args[1];
                listener.onQuestionDetailsFetchFailed();
                return null;
            }
        }).when(mFetchQuestionDetailsEndpointMock).fetchQuestionDetails(anyString(), any(FetchQuestionDetailsEndpoint.Listener.class));
    }

    private void checkQuestionDetails(List<QuestionDetails> questionDetailsList, QuestionDetails questionDetails){
        for (QuestionDetails mQuestionDetails : questionDetailsList){
            assertThat(mQuestionDetails, is(questionDetails));
        }
    }


    private void setTimeOut(Long timeOut) {
        when(mTimeProvider.getCurrentTimestamp()).thenReturn(timeOut);
    }



}