package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class FetchUserProfileUseCaseSyncTest {

    public static final String USER_ID = "userId";
    public static final String FULL_NAME = "fullName";
    public static final String IMAGE_URL = "imageUrl";

    FetchUserProfileUseCaseSync SUT;
    UserProfileHttpEndpointSyncTd mUserProfileHttpEndpointSyncTd;
    UsersCacheTd mUsersCacheTd;



    @Before
    public void setUp() throws Exception {
        mUserProfileHttpEndpointSyncTd = new UserProfileHttpEndpointSyncTd();
        mUsersCacheTd= new UsersCacheTd();
        SUT = new FetchUserProfileUseCaseSync(mUserProfileHttpEndpointSyncTd, mUsersCacheTd);
    }

    //if fetch user profile success passed userId to the endpoint

    @Test
    public void fetchUserSync_success_userIdPassedToEndpoint() {
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(mUserProfileHttpEndpointSyncTd.mUserId, is(USER_ID));
    }

    //if fetch user profile cache the user profile

    @Test
    public void fetchUser_success_cacheUserProfile() {
        SUT.fetchUserProfileSync(USER_ID);
        User u = mUsersCacheTd.getUser(USER_ID);
        assertThat(u.getUserId(), is(USER_ID));
        assertThat(u.getFullName(), is(FULL_NAME));
        assertThat(u.getImageUrl(), is(IMAGE_URL));
    }


    //if fetch user profile cache fail

    @Test
    public void fetchUser_generalError_userNotCache() {
        mUserProfileHttpEndpointSyncTd.isGeneralError = true;
        SUT.fetchUserProfileSync(USER_ID);
        User u =  mUsersCacheTd.getUser(USER_ID);
        assertNull(u);
    }

    @Test
    public void fetchUser_authError_userNotCache() {
        mUserProfileHttpEndpointSyncTd.isAuthError = true;
        SUT.fetchUserProfileSync(USER_ID);
        User u =  mUsersCacheTd.getUser(USER_ID);
        assertNull(u);
    }

    @Test
    public void fetchUser_serverError_userNotCache() {
        mUserProfileHttpEndpointSyncTd.isServerError = true;
        SUT.fetchUserProfileSync(USER_ID);
        User u =  mUsersCacheTd.getUser(USER_ID);
        assertNull(u);
    }

    @Test
    public void fetchUser_generalError_generalErrorReturned() {
        mUserProfileHttpEndpointSyncTd.isGeneralError = true;
        FetchUserProfileUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUser_authError_authErrorReturned() {
        mUserProfileHttpEndpointSyncTd.isAuthError = true;
        FetchUserProfileUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUser_serverError_serveErrorReturned() {
        mUserProfileHttpEndpointSyncTd.isServerError = true;
        FetchUserProfileUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUser_networkError_userNotCache() {
        mUserProfileHttpEndpointSyncTd.isNetworkError =  true;
        SUT.fetchUserProfileSync(USER_ID);
        User u =  mUsersCacheTd.getUser(USER_ID);
        assertNull(u);
    }

    @Test
    public void fetchUser_networkError_networkErrorReturned() {
        mUserProfileHttpEndpointSyncTd.isNetworkError =  true;
        FetchUserProfileUseCaseSync.UseCaseResult useCaseResult = SUT.fetchUserProfileSync(USER_ID);
        assertThat(useCaseResult, is(FetchUserProfileUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }

    private static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync{
        String mUserId= "";
        boolean isGeneralError;
        boolean isAuthError;
        boolean isServerError;
        boolean isNetworkError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            mUserId = userId;
            if(isGeneralError){
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR,"","", "");
            }else if(isAuthError){
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR,"","", "");
            }else if(isServerError){
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR,"","", "");
            }else if(isNetworkError){
                throw  new NetworkErrorException();
            }
            else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, FULL_NAME, IMAGE_URL);
            }
        }
    }

    private static class UsersCacheTd implements UsersCache{

        List<User> mUsers = new ArrayList<>(1);

        @Override
        public void cacheUser(User user) {
            User u = getUser(user.getUserId());
            if(null != u){
                mUsers.remove(user);
            }
            mUsers.add(user);
        }

        @Nullable
        @Override
        public User getUser(String userId) {
            User user = null;
            for (User u:mUsers) {
                if (u.getUserId().equals(userId)){
                    user = u;
                }
            }
            return user;
        }
    }
}