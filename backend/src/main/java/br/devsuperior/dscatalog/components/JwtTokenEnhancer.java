package br.devsuperior.dscatalog.components;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import br.devsuperior.dscatalog.entities.User;
import br.devsuperior.dscatalog.repositories.UserRepository;
/**
 *  Classe turbinadora de Tokens JWT.<br>
 *  <p>Sua funcionalidade consiste em entrar no ciclo de vida do token JWT e adicionar
 *  novos campos que possam ser do interesse de sua aplicação
 *  além dos campos já pré-existentes por default.</p>
 *  
 *  @author Fagner Cruz (by DEV Superior)
 *  @since 06/12/2022
 * 
 * */
@SuppressWarnings("deprecation")
@Component
public class JwtTokenEnhancer implements TokenEnhancer {
	
	@Autowired
	private UserRepository repository;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		User user = repository.findByEmail(authentication.getName());
		
		//campos para adicionar
		Map<String, Object> map = new HashMap<>();
		map.put("user_first_name", user.getFirstName());
		map.put("user_id", user.getId());
		
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(map);
		
		return accessToken;
	}

}
