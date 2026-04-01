<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password'); section>
  <#if section = "header">
    Sign in with iRODS
  <#elseif section = "form">
    <form id="kc-irods-login-form" action="${url.loginAction}" method="post">
      <div class="form-group">
        <label for="username">Username</label>
        <input tabindex="1" id="username" name="username" type="text" autofocus autocomplete="username" />
      </div>
      <div class="form-group">
        <label for="password">Password</label>
        <input tabindex="2" id="password" name="password" type="password" autocomplete="current-password" />
      </div>
      <div class="form-group">
        <label for="loginType">Login Type</label>
        <select tabindex="3" id="loginType" name="loginType" class="form-control">
            <option value="native">native login</option>
            <option value="pam">pam login</option>
        </select>
      </div>
      <div class="form-group">
        <input tabindex="4" class="btn btn-primary btn-block btn-lg" name="login" id="kc-login" type="submit" value="Log in" />
      </div>
    </form>
  </#if>
</@layout.registrationLayout>
