package com.test.juxiaohui.mdxc.mediator;


/**
 *
 */
public interface IRegisterMediator {
   
	public void addPhoneNumberView();
    public void addPasswordView();
    public void addConfirmPasswordView();
    public void addGetCheckcodeView();
    public void addRegisterButton();

    /**
     * 添加国家代码选择
     */
    public void addCountryCodeView();
    public void getCheckcode();
    public void confirm();
    public void cancel();
}
