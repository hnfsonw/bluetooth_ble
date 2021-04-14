package com.ronda.tech.upgrade.view.inter;

public interface IForgetPasswoldAView {
    String getInputInfo();
    void getCheckCodeSuccessful();
    void getcheckCodeFail(String failMsg);
    void summitSuccessful();
    void summitFail(String msg);
}
