package com.techyourchance.unittesting.screens.questiondetails;

import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase;
import com.techyourchance.unittesting.questions.QuestionDetails;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.techyourchance.unittesting.testdata.QuestionsTestData.getQuestionsDetails;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QuestionDetailsControllerTest {

    private static QuestionDetails QUESTION_DETAILS = getQuestionsDetails();

    QuestionDetailsController SUT;
    UseCaseTd mUseCaseTd;

    @Mock
    ScreensNavigator mScreensNavigatorMock;
    @Mock
    ToastsHelper mToastsHelperMock;
    @Mock
    QuestionDetailsViewMvc mQuestionDetailsViewMvcMock;

    @Before
    public void setUp() throws Exception {
        mUseCaseTd = new UseCaseTd();
        SUT = new QuestionDetailsController(mUseCaseTd, mScreensNavigatorMock, mToastsHelperMock);
        SUT.bindView(mQuestionDetailsViewMvcMock);
        SUT.bindQuestionId(QUESTION_DETAILS.getId());
    }

    // On start - fetch question details
    // On successful - bind the question details to the view

    @Test
    public void onStart_success_fetchQuestionDetailsAndBindToTheView() {
        SUT.onStart();

        verify(mQuestionDetailsViewMvcMock).hideProgressIndication();
        verify(mQuestionDetailsViewMvcMock).bindQuestion(any(QuestionDetails.class));
    }

    // On failed questions fetch - error toast is shown

    @Test
    public void onStart_failure_errorToastShown() {
        failure();
        SUT.onStart();

        verify(mQuestionDetailsViewMvcMock).hideProgressIndication();
        verify(mToastsHelperMock).showUseCaseError();
    }

    @Test(expected = RuntimeException.class)
    public void onStart_emptyQuestionId_throwException() {
        SUT.bindQuestionId(null);
        SUT.onStart();
    }

    // On start - register listener

    @Test
    public void onStart_success_listenerRegistered() {
        SUT.onStart();

        verify(mQuestionDetailsViewMvcMock).registerListener(any(QuestionDetailsViewMvc.Listener.class));
        assertThat(mUseCaseTd.checkListener(SUT), is(true));
    }

    @Test
    public void onStart_success_showProgressIndication() {
        SUT.onStart();

        verify(mQuestionDetailsViewMvcMock).showProgressIndication();
    }

    // On stop - unregister listener

    @Test
    public void onStop_success_listenerUnregistered() {
        SUT.onStart();
        SUT.onStop();

        verify(mQuestionDetailsViewMvcMock).unregisterListener(any(QuestionDetailsViewMvc.Listener.class));
        assertThat(mUseCaseTd.checkListener(SUT), is(false));
    }


    // On question - clicked


    @Test
    public void onNavigationUpClicked_success_navigateToQuestionList() {
        SUT.onNavigateUpClicked();

        verify(mScreensNavigatorMock).navigateUp();
    }

    private void failure(){
        mUseCaseTd.failure = true;
    }

    private class UseCaseTd extends FetchQuestionDetailsUseCase{

        boolean failure;
        public UseCaseTd() {
            super(null);
        }

        @Override
        public void fetchQuestionDetailsAndNotify(String questionId) {
            for(Listener listener : getListeners()){
                if (!failure){
                    listener.onQuestionDetailsFetched(QUESTION_DETAILS);
                }else {
                    listener.onQuestionDetailsFetchFailed();
                }
            }
        }

        public boolean  checkListener(Listener candidate){
            return getListeners().contains(candidate);
        }
    }
    /*
    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String questionId = (String) args[0];
                mListenerMock.onQuestionDetailsFetched(questionSchemaFromQuestion(QUESTION,"Body"));
                return null;
            }
        }).when(mFetchQuestionDetailsUseCaseMock).fetchQuestionDetailsAndNotify(anyString());
    }
     */

}