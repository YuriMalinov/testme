package ru.smarty.testme.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.smarty.testme.security.UserDetailsMapper
import ru.smarty.testme.security.UserDetailsServiceImpl

@Configuration
open class SecurityConfig : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .anyRequest().fullyAuthenticated()
                .and().httpBasic()
                .and().csrf().disable()
    }

    @Configuration
    open protected class AuthenticationConfiguration : GlobalAuthenticationConfigurerAdapter() {
        @Autowired
        lateinit var ldapConfig: LdapConfig

        @Autowired
        lateinit var passwordConfig: PasswordConfig

        @Autowired
        lateinit var userDetailsMapper: UserDetailsMapper

        @Autowired
        lateinit var userDetailsService: UserDetailsServiceImpl

        @Bean
        open fun passwordEncoder() = BCryptPasswordEncoder()

        override fun init(auth: AuthenticationManagerBuilder) {
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
                    .userDetailsContextMapper(userDetailsMapper)
                }
            } else {
                auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder())
            }
        }

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

        @ConfigurationProperties("auth.password")
        @Service
        class PasswordConfig {
            lateinit var user: String
            lateinit var password: String
        }
    }
}
