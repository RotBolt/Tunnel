package io.rot.labs.tunnel.regisrtyClass


/**
 *  Registry Class
 *  Class whose Methods are annotated with @Subscribe annotation
 *  needs to be registered.
 *  This the wrapper class to do the registry of the class
 */
data class RegistryClass<T : Any>(
    val registryObj: T
)