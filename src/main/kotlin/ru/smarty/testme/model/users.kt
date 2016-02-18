package ru.smarty.testme.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Suppress("unused")
@Entity
@Table(schema = "tm", name = "app_user")
open class AppUser : UserDetails {
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int = 0

    @get:Size(min = 1)
    @get:NotNull
    open var login: String = ""

    @get:Size(min = 1)
    @get:NotNull
    open var userName: String = ""

    @get:Column(name = "password")
    open var userPassword: String? = null // Interface clash

    @get:Column(name = "is_admin")
    open var isAdmin: Boolean = false

    @get:Column(name = "is_active")
    open var isActive: Boolean = true

    @Transient
    @JsonIgnore
    override fun getUsername(): String = userName

    @Transient
    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean = true

    @Transient
    @JsonIgnore
    override fun isAccountNonExpired(): Boolean = true

    @Transient
    @JsonIgnore
    override fun isAccountNonLocked(): Boolean = isActive

    @Transient
    @JsonIgnore
    override fun getAuthorities(): List<GrantedAuthority> {
        return if (isAdmin)
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"), SimpleGrantedAuthority("ROLE_USER"))
        else
            listOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    @Transient
    @JsonIgnore
    override fun isEnabled(): Boolean = true

    @Transient
    @JsonIgnore
    override fun getPassword(): String? = userPassword
}