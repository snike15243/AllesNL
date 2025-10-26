package com.allesnl.user_service.dto;

public class LoginResponse {
    private boolean success;
    private String message;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public LoginResponse() {
    }

    public LoginResponse(boolean success, String message, String firstName, String lastName, String phoneNumber) {
        this.success = success;
        this.message = message;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}