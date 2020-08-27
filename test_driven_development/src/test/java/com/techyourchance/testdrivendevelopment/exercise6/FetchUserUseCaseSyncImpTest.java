package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchUserUseCaseSyncImpTest {
    /*
    The requirements:
            1) If the user with given user ID is not in the cache then it should be fetched from the server.
            2) If the user fetched from the server then it should be stored in the cache before returning to the caller.
            3) If the user is in the cache then cached record should be returned without polling the server.
  */
    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final User USER = new User(USER_ID, USERNAME);

    FetchUserUseCaseSyncImp SUT;

    @Mock
    UsersCache mUsersCacheMock;

    FetchUserHttpEndpointSyncDoubleTest mFetchUserHttpEndpointSyncTd;
    
    @Before
    public void setUp() throws Exception {
        mFetchUserHttpEndpointSyncTd =  new FetchUserHttpEndpointSyncDoubleTest();
        SUT = new FetchUserUseCaseSyncImp(mFetchUserHttpEndpointSyncTd, mUsersCacheMock);
        userNotCache();
    }

    //Pass userId to the interface

    @Test
    public void fetchUser_success_userIdPassedToEndPoint() {
        SUT.fetchUserSync(USER_ID);

        assertThat(mFetchUserHttpEndpointSyncTd.mUserId, is(USER_ID));
    }


    //User with ID is not cache

    @Test
    public void fetchUser_notCacheUser_correctResultReturned() {
        UseCaseResult result = SUT.fetchUserSync(USER_ID);

        assertThat(result.getStatus(),is(Status.SUCCESS));
        assertThat(result.getUser(),is(USER));
    }

    @Test
    public void fetchUser_notCacheUser_cacheUser() {
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

        SUT.fetchUserSync(USER_ID);

        verify(mUsersCacheMock).cacheUser(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue(), is(USER));
    }

    @Test
    public void fetchUser_notCacheUserServerError_FailureReturned() {
        serverError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);

        checkFailure(result);
    }

    @Test
    public void fetchUser_notCacheUserAuthError_FailureReturned() {
        authError();

        UseCaseResult result = SUT.fetchUserSync(USER_ID);

        checkFailure(result);
    }

    @Test
    public void fetchUser_notCacheUserNetworkError_FailureReturned() {
        networkError();

        UseCaseResult result = SUT.fetchUserSync(USER_ID);

        verify(mUsersCacheMock, never()).cacheUser(any(User.class));
        assertThat(result.getStatus(), is(Status.NETWORK_ERROR));
        assertThat(result.getUser(),is(nullValue()));
    }

    @Test
    public void fetchUser_userInCache_successReturned() {
        userCache();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);

        assertThat(result.getStatus(), is(Status.SUCCESS));
        assertThat(result.getUser(), is(USER));
        verify(mUsersCacheMock, never()).cacheUser(any(User.class));
    }

    private void userNotCache(){
        when(mUsersCacheMock.getUser(USER_ID)).thenReturn(null);
    }

    private void userCache(){
        when(mUsersCacheMock.getUser(USER_ID)).thenReturn(USER);
    }

    private void authError(){
        mFetchUserHttpEndpointSyncTd.isAuthError = true;
    }


    private void serverError(){
        mFetchUserHttpEndpointSyncTd.isServerError = true;
    }


    private void networkError(){
        mFetchUserHttpEndpointSyncTd.isNetworkError = true;
    }

    private void checkFailure(UseCaseResult result) {
        verify(mUsersCacheMock, never()).cacheUser(any(User.class));
        assertThat(result.getStatus(), is(Status.FAILURE));
        assertThat(result.getUser(), is(nullValue()));
    }

    private class FetchUserHttpEndpointSyncDoubleTest implements FetchUserHttpEndpointSync{

        int timeOfFetchUser=0;
        String mUserId = "";
        boolean isAuthError;
        boolean isServerError;
        boolean isNetworkError;

        @Override
        public EndpointResult fetchUserSync(String userId) throws NetworkErrorException {
            timeOfFetchUser++;
            mUserId = userId;
            if(isAuthError){
                return new EndpointResult(EndpointStatus.AUTH_ERROR, "","");
            }else if(isServerError){
                return new EndpointResult(EndpointStatus.GENERAL_ERROR, "","");
            }else if(isNetworkError){
                throw new NetworkErrorException();
            }

            return new EndpointResult(EndpointStatus.SUCCESS, USER_ID, USERNAME);
        }
    }
}