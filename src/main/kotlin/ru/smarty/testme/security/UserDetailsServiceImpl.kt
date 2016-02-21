package ru.smarty.testme.security

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.smarty.testme.model.AppUser
import ru.smarty.testme.repositories.AppUserRepository
import java.util.concurrent.locks.ReentrantLock

@Service
class UserDetailsServiceImpl @Autowired constructor(
        private val userRepository: AppUserRepository,
        private val passwordEncoder: BCryptPasswordEncoder
) : UserDetailsService {
    private var checkedEmptiness = false
    private val checkNewLock = ReentrantLock()

    override fun loadUserByUsername(userName: String): UserDetails? {
        val details = userRepository.findByUserName(userName)

        val logger = LoggerFactory.getLogger(this.javaClass)
        logger.debug("Checking $userName, $details found, checkedEmptiness = $checkedEmptiness")
        // Automatically initialize user if needed
        if (details == null && !checkedEmptiness && userName == "admin") {
            synchronized(checkNewLock) {
                val count = userRepository.count()
                checkedEmptiness = true

                if (count == 0L) {
                    logger.info("Empty database: creating user 'admin; with password 'admin'.")
                    val admin = AppUser().apply {
                        fullName = "Admin"
                        this.userName = "admin"
                        userPassword = passwordEncoder.encode("admin")
                        isAdmin = true
                    }

                    return userRepository.save(admin)
                }
            }
        }

        return details
    }
}