package ru.smarty.testme.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ldap.core.DirContextAdapter
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper
import org.springframework.stereotype.Service
import ru.smarty.testme.model.AppUser
import ru.smarty.testme.repositories.AppUserRepository
import ru.smarty.testme.utils.orCreate

@Service
open class UserDetailsMapper @Autowired constructor(
        private val userRepository: AppUserRepository
) : UserDetailsContextMapper {
    override fun mapUserToContext(userDetails: UserDetails, context: DirContextAdapter) {
    }

    override fun mapUserFromContext(context: DirContextOperations, userName: String, authorities: MutableCollection<out GrantedAuthority>): AppUser {
        val user = userRepository.findByUserName(userName) orCreate {
            val appUser = AppUser()
            appUser.login = userName
            appUser.userName = context.getObjectAttribute("cn") as String
            appUser.isAdmin = userRepository.count() == 0L
            userRepository.save(appUser)
        }

        return user
    }
}