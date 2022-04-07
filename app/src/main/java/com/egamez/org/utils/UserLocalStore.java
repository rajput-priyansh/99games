// For store login data in local storage
package com.egamez.org.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.egamez.org.models.CurrentUser;

public class UserLocalStore {

    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(CurrentUser cUser) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("memberid", cUser.memberId);
        userLocalDatabaseEditor.putString("username", cUser.userName);
        userLocalDatabaseEditor.putString("password", cUser.password);
        userLocalDatabaseEditor.putString("email", cUser.email);
        userLocalDatabaseEditor.putString("phone", cUser.phone);
        userLocalDatabaseEditor.putString("token", cUser.token);
        userLocalDatabaseEditor.apply();
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.apply();
    }

    public CurrentUser getLoggedInUser() {
        String memberid = userLocalDatabase.getString("memberid", "");
        String username = userLocalDatabase.getString("username", "");
        String password = userLocalDatabase.getString("password", "");
        String email = userLocalDatabase.getString("email", "");
        String phone = userLocalDatabase.getString("phone", "");
        String token = userLocalDatabase.getString("token", "");
        CurrentUser cUser = new CurrentUser(memberid, username, password,email,phone,token);
        return cUser;
    }
}
