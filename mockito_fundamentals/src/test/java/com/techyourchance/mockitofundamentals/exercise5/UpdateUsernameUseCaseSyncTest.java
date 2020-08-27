package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class UpdateUsernameUseCaseSyncTest {

    private static String USER_ID = "userId";
    private static String USERNAME = "username";

    UpdateUsernameUseCaseSync SUT;
    UpdateUsernameHttpEndpointSync mUpdateUsernameHttpEndpointSyncMock;
    UsersCache mUsersCacheMock;
    EventBusPoster mEventBusPosterMock;

    @Before
    public void setUp(){
        mUpdateUsernameHttpEndpointSyncMock = mock(UpdateUsernameHttpEndpointSync.class);
        mUsersCacheMock = mock(UsersCache.class);
        mEventBusPosterMock = mock(EventBusPoster.class);
        SUT = new UpdateUsernameUseCaseSync(mUpdateUsernameHttpEndpointSyncMock,mUsersCacheMock, mEventBusPosterMock);
    }

    @Test
    public void updateUsername_success_userIdAndUsernameSendToEndpoint() throws NetworkErrorException {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        success();
        SUT.updateUsernameSync(USER_ID,USERNAME);

        verify(mUpdateUsernameHttpEndpointSyncMock, times(1))
                .updateUsername(argumentCaptor.capture(),argumentCaptor.capture());
        List<String> captures = argumentCaptor.getAllValues();
        assertThat(USER_ID, is(captures.get(0)));
        assertThat(USERNAME, is(captures.get(1)));
    }


    @Test
    public void updateUsername_success_userCache() throws NetworkErrorException {
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

        success();
        SUT.updateUsernameSync(USER_ID,USERNAME);

        verify(mUsersCacheMock).cacheUser(argumentCaptor.capture());
        User user = argumentCaptor.getValue();
        assertThat(user.getUserId(), is(USER_ID));
        assertThat(user.getUsername(), is(USERNAME));
    }

    @Test
    public void updateUsername_generalError_userNotCache() throws NetworkErrorException {
        generalError();
        SUT.updateUsernameSync(USER_ID,USERNAME);

        verifyNoMoreInteractions(mUsersCacheMock);
    }

    @Test
    public void updateUsername_authError_userNotCache() throws NetworkErrorException {
        authError();

        SUT.updateUsernameSync(USER_ID,USERNAME);

        verifyNoMoreInteractions(mUsersCacheMock);
    }

    @Test
    public void updateUsername_serverError_userNotCache() throws NetworkErrorException {
        serverError();
        SUT.updateUsernameSync(USER_ID,USERNAME);

        verifyNoMoreInteractions(mUsersCacheMock);
    }

    @Test
    public void updateUsername_generalError_failReturned() throws NetworkErrorException {
        generalError();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult = SUT.updateUsernameSync(USER_ID, USERNAME);

        assertThat(useCaseResult, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_authError_failReturned() throws NetworkErrorException {
        authError();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult = SUT.updateUsernameSync(USER_ID, USERNAME);

        assertThat(useCaseResult, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }


    @Test
    public void updateUsername_serverError_failReturned() throws NetworkErrorException {
        serverError();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult = SUT.updateUsernameSync(USER_ID, USERNAME);

        assertThat(useCaseResult, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_networkError_networkErrorReturned() throws NetworkErrorException {
        success();
        networkError();
        UpdateUsernameUseCaseSync.UseCaseResult useCaseResult = SUT.updateUsernameSync(USER_ID, USERNAME);

        assertThat(useCaseResult, is(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }

    private void success() throws NetworkErrorException {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult
                        (UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS, USER_ID, USERNAME));
    }

    private void generalError() throws NetworkErrorException {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult
                        (UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR, "", ""));
    }

    private void authError() throws NetworkErrorException {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult
                        (UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR, "", ""));
    }

    private void serverError() throws NetworkErrorException {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult
                        (UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR, "", ""));
    }

    private void networkError() throws NetworkErrorException {
        when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenThrow(new NetworkErrorException());
    }

}