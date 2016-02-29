package com.gd.app.entity;

import java.io.Serializable;

public interface ServletInfoBean extends Serializable {
    public String getErrorMessage();
    public void setErrorMessage(String errorMessage);
}
