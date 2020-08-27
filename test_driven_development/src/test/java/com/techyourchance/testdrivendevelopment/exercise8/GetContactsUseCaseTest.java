package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/*
Description:
Your goal is to implement FetchContactsUseCase class using Uncle Bob's TDD technique. This class
must be Observable and notify it's observers (listeners) about completion of an async flow
through method calls.

The three rules of TDD are:
1) You are not allowed to write any production code unless it is to make a failing unit test pass
2) You are not allowed to write any more of a unit test than is sufficient to fail; and compilation failures are failures
3) You are not allowed to write any more production code than is sufficient to pass the one failing unit test

The requirements:
1) If the server request completes successfully, then registered listeners should be notified with correct data.
2) If the server request fails for any reason except network error, then registered listeners should be notified about a failure.
3) If the server request fails due to network error, then registered listeners should be notified about a network error specifically.

You should:
1) Create a new class FetchContactsUseCase WITHOUT writing any actual functionality.
2) Create a new test class.
3) Test drive the implementation of FetchContactsUseCase according to Uncle Bob's three rules of TDD.
   Always run all the tests to make sure that further changes don't break the existing functionality.
4) Refactor the production code.
5) Run the tests to make sure that all of them pass after the refactoring.
6) Refactor the tests code.
7) Run the tests to make sure that all of them pass after the refactoring.
 */

@RunWith(MockitoJUnitRunner.class)
public class GetContactsUseCaseTest {

    private static final String FILTER_TERM = "filter term";
    private static final double AGE = 25.0;
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PHONE_NUMBER = "phone number";
    private static final String IMAGE = "image";

    private GetContactsUseCase SUT;

    @Captor
    ArgumentCaptor<String> argumentCaptor;
    @Captor
    ArgumentCaptor<List<Contact>> mAcListContacts;
    @Mock
    GetContactsHttpEndpoint mGetContactsHttpEndpointMock;
    @Mock
    GetContactsUseCase.Listener mListenerMock1;
    @Mock
    GetContactsUseCase.Listener mListenerMock2;


    @Before
    public void setUp() throws Exception {
        SUT = new GetContactsUseCase(mGetContactsHttpEndpointMock);
        success();
    }

    @Test
    public void getContacts_correctFilterTermPassedToEndpoint() {
        SUT.getContactsSync(FILTER_TERM);

        verify(mGetContactsHttpEndpointMock).getContacts(argumentCaptor.capture(), any(Callback.class));
        assertThat(argumentCaptor.getValue(), is(FILTER_TERM));
    }

    @Test
    public void getContacts_success_observersNotifiedWithCorrectResult() {
        List<List<Contact>> captures;
        List<Contact> cap1;
        List<Contact> cap2;
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.getContactsSync(FILTER_TERM);

        verify(mListenerMock1).onGetContacts(mAcListContacts.capture());
        verify(mListenerMock2).onGetContacts(mAcListContacts.capture());
        captures = mAcListContacts.getAllValues();
        cap1 = captures.get(0);
        cap2 = captures.get(1);
        assertThat(cap1, is(getContacts()));
        assertThat(cap2, is(getContacts()));
    }

    @Test
    public void getContact_success_unsubscribedObserversNotNotified() {

        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.unregisterListener(mListenerMock2);
        SUT.getContactsSync(FILTER_TERM);

        verify(mListenerMock1).onGetContacts(mAcListContacts.capture());
        verifyNoMoreInteractions(mListenerMock2);
        assertThat(mAcListContacts.getValue(),is(getContacts()));
    }

    @Test
    public void getContact_generalError_observerNotifiedOfFailure() {

        generalError();
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.getContactsSync(FILTER_TERM);

        verify(mListenerMock1).onGetContactsFailed();
        verify(mListenerMock2).onGetContactsFailed();
    }

    @Test
    public void getContact_networkError_observerNotifiedOfNetworkError() {
        networkError();
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.getContactsSync(FILTER_TERM);

        verify(mListenerMock1).onGetContactsNetworkError();
        verify(mListenerMock2).onGetContactsNetworkError();
    }


    public void success(){
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsSucceeded(getContactsSuccess());
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }


    private void generalError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(),any(Callback.class));
    }

    private void networkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(),any(Callback.class));

    }

    private List<ContactSchema> getContactsSuccess() {
        List<ContactSchema> contactSchemas = new ArrayList<>();
        contactSchemas.add(new ContactSchema(ID, NAME, PHONE_NUMBER, IMAGE, AGE));
        return contactSchemas;
    }
    private List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(ID, NAME, IMAGE));
        return contacts;
    }
}