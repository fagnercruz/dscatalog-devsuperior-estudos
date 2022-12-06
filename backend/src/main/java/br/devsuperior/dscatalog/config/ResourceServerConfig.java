package br.devsuperior.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@SuppressWarnings("deprecation")
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
	@Autowired
	private JwtTokenStore tokenStore;
	
	
	//constantes para os endpoints
	private static final String[] PUBLIC_ROUTES = {"/oauth/token"};
	private static final String[] OPERATOR_OR_ADMIN = {"/products/**","/categories/**"};
	private static final String[] ADMIN_ROUTES_ONLY = {"/users/**"};

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers(PUBLIC_ROUTES).permitAll()
			.antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll() // libeta os endpoints para todo mundo em req. GET (apenas listagens)
			.antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN") // libera os endpoints para operadores e admins (outros verbos do crud)
			.antMatchers(ADMIN_ROUTES_ONLY).hasRole("ADMIN")
			.anyRequest().authenticated();
			
	}

}
