//For current login user data
package com.egamez.org.models;

public class CurrentUser {

    public String memberId;
    public String userName;
    public String password;
    public String email;
    public String phone;
    public  String token;

    public CurrentUser(String memberId, String userName, String password, String email,String phone,String token) {
        this.memberId = memberId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.token=token;
    }

    public String getMemberid() {
        return memberId;
    }

    public void setMemberid(String memberId) {
        this.memberId = memberId;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
