package ru.smarty.testme.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.ldap.core.support.LdapContextSource
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch
import org.springframework.security.ldap.userdetails.LdapUserDetailsService
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices
import org.springframework.stereotype.Service
import ru.smarty.testme.repositories.AppUserRepository
import ru.smarty.testme.security.UserDetailsMapper
import ru.smarty.testme.security.UserDetailsServiceImpl

@Configuration
open class SecurityConfig : WebSecurityConfigurerAdapter() {
    private val SECURITY_KEY = "cn18idbn1gd--1bbvd8"

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/external/**", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
                .and().formLogin()
                .apply {
                    loginPage("/login")
                    permitAll()
                }
                .and().rememberMe().rememberMeServices(rememberMeServices(SECURITY_KEY)).key(SECURITY_KEY)
                .and().logout().permitAll()
                .and().csrf().disable()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        if (ldapConfig.enabled) {
            with (ldapConfig) {
                auth.ldapAuthentication()
                        .userSearchBase(userSearchBase)
                        .userSearchFilter(userSearchFilter)
                        .contextSource()
                        .url(url)
                        .managerDn(managerDn)
                        .managerPassword(managerPassword)
                        .and()
                        .userDetailsContextMapper(detailsMapper)
            }
        } else {
            auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder())
        }
    }

    @Autowired
    lateinit var detailsMapper: UserDetailsMapper

    @Autowired
    lateinit var myUserDetailsService: UserDetailsServiceImpl

    @Autowired
    lateinit var ldapConfig: LdapConfig

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Bean
    open fun rememberMeServices(internalSecretKey: String): RememberMeServices {
        val repositoryImpl = JdbcTokenRepositoryImpl()
        repositoryImpl.jdbcTemplate = jdbcTemplate
        val services = PersistentTokenBasedRememberMeServices(internalSecretKey, myUserDetailsService, repositoryImpl)
        services.setAlwaysRemember(true)
        services.setTokenValiditySeconds(60 * 60 * 24 * 30)
        return services;
    }

    @Bean
    open fun getLdapContext(): LdapContextSource {
        val source = LdapContextSource();
        source.setUserDn(ldapConfig.managerDn);
        source.setPassword(ldapConfig.managerPassword);
        source.setUrl(ldapConfig.url);
        return source;
    }

    @Bean
    open fun passwordEncoder() = BCryptPasswordEncoder()

    @ConfigurationProperties("auth.ldap")
    @Service
    class LdapConfig {
        var enabled = false
        lateinit var url: String
        lateinit var managerDn: String
        lateinit var managerPassword: String
        lateinit var userSearchBase: String
        lateinit var userSearchFilter: String
    }
}
