/*
 * SonarQube LDAP Plugin
 * Copyright (C) 2009 SonarSource
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.ldap.windows;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.security.ExternalUsersProvider;
import org.sonar.api.security.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;

public class WindowsUsersProviderTest {
  @Test(expected = NullPointerException.class)
  public void nullArgumentCheckOnConstructor() {
    new WindowsUsersProvider(null);
  }

  @Test
  public void doGetUserDetailsTests() {
    UserDetails expectedUserDetails = new UserDetails();

    this.runDoGetUserDetailsTest(null, null);
    this.runDoGetUserDetailsTest("", null);

    this.runDoGetUserDetailsTest("user", null);
    this.runDoGetUserDetailsTest("domain\\user", expectedUserDetails);
    this.runDoGetUserDetailsTest("user@domain", expectedUserDetails);
  }

  private void runDoGetUserDetailsTest(String userName, UserDetails expectedUserDetails) {

    WindowsAuthenticationHelper windowsAuthenticationHelper = Mockito.mock(WindowsAuthenticationHelper.class);

    HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
    ExternalUsersProvider.Context context = new ExternalUsersProvider.Context(userName, httpServletRequest);

    if (StringUtils.isBlank(userName)) {
      Mockito.when(windowsAuthenticationHelper.getSsoUserDetails(httpServletRequest)).thenReturn(expectedUserDetails);
    } else {
      Mockito.when(windowsAuthenticationHelper.getUserDetails(userName)).thenReturn(expectedUserDetails);
    }

    WindowsUsersProvider usersProvider = new WindowsUsersProvider(windowsAuthenticationHelper);
    UserDetails userDetails = usersProvider.doGetUserDetails(context);

    assertThat(userDetails).isEqualTo(expectedUserDetails);
    if (StringUtils.isBlank(userName)) {
      Mockito.verify(windowsAuthenticationHelper, Mockito.times(1)).getSsoUserDetails(httpServletRequest);
    } else {
      Mockito.verify(windowsAuthenticationHelper, Mockito.times(1)).getUserDetails(userName);
    }
  }

}
