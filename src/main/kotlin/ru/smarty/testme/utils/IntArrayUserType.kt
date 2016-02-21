package ru.smarty.testme.utils

import org.hibernate.engine.spi.SessionImplementor
import org.hibernate.usertype.UserType
import java.io.Serializable
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types
import java.util.*

class IntArrayUserType : UserType {
    override fun sqlTypes(): IntArray {
        return SQL_TYPES
    }

    override fun returnedClass(): Class<*> = Array<Int>::class.java

    override fun nullSafeGet(
            resultSet: ResultSet,
            names: Array<String>,
            session: SessionImplementor,
            owner: Any): List<Int>? {
        val sqlArray = resultSet.getArray(names[0]) ?: return ArrayList()

        @Suppress("UNCHECKED_CAST")
        val array = sqlArray.array as Array<Int>
        return arrayListOf(*array)
    }

    override fun nullSafeSet(statement: PreparedStatement, value: Any?,
                             index: Int, session: SessionImplementor) {

        if (value == null) {
            statement.setNull(index, SQL_TYPES[0])
        } else {
            @Suppress("UNCHECKED_CAST")
            val castObject = value as List<Int>
            val array = session.connection().createArrayOf("int", castObject.toTypedArray())
            statement.setArray(index, array)
        }
    }

    override fun deepCopy(value: Any?): Any? {
        if (value == null) return value

        return ArrayList(value as List<*>)
    }

    override fun isMutable() = true

    override fun assemble(arg0: Serializable, arg1: Any): Any? {
        return null
    }

    override fun disassemble(arg0: Any): Serializable? {
        return null
    }

    override fun equals(x: Any?, y: Any?): Boolean {
        if (x === y) {
            return true
        } else if (x == null || y == null) {
            return false
        } else {
            return x.equals(y)
        }
    }

    override fun hashCode(x: Any): Int {
        return x.hashCode()
    }

    override fun replace(
            original: Any,
            target: Any,
            owner: Any): Any {
        return original
    }

    companion object {

        /**
         * Constante contenant le type SQL "Array".
         */
        protected val SQL_TYPES = intArrayOf(Types.ARRAY)
    }
}