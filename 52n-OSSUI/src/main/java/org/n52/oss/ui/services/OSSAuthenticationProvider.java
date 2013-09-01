package org.n52.oss.ui.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service("userAuthService")
public class OSSAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication arg0)
			throws AuthenticationException {
		String username = arg0.getName();
		String password = arg0.getCredentials().toString();

		if (authenticateOSS(username, password) != null) {
			final List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
			grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
			final UserDetails principal = new User(username, password,
					grantedAuths);
			final Authentication auth = new UsernamePasswordAuthenticationToken(
					principal, password, grantedAuths);
			return auth;

		} else
			throw new UsernameNotFoundException(
					"Wrong username/password combination");
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return arg0.equals(UsernamePasswordAuthenticationToken.class);
	}

	public class AuthToken {
		String auth_token;
	}

	private String authenticateOSS(String username, String password) {
		try {
			HttpPost post = new HttpPost(
					"http://localhost:8080/OpenSensorSearch/api/user/login");
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("username", username));
			pairs.add(new BasicNameValuePair("password", password));
			post.setEntity(new UrlEncodedFormEntity(pairs));

			HttpClient client = new DefaultHttpClient();
			HttpResponse resp = client.execute(post);
			StringBuilder result = new StringBuilder();
			String s = null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					resp.getEntity().getContent()));
			while ((s = reader.readLine()) != null)
				result.append(s);

			AuthToken token = new Gson().fromJson(result.toString(),
					AuthToken.class);
			return token.auth_token;
		} catch (Exception e) {
			return null;
		}
	}

}
