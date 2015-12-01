package com.jenjinstudios.io.authentication;

import com.jenjinstudios.io.ExecutionContext;

import java.util.Collections;
import java.util.Map;

/**
 * Interface defining several methods that are useful for authentication.
 *
 * @author Caleb Brinkman
 */
public interface Authenticator<T extends ExecutionContext>
{
    /** This constant is provided an a convenience, to specify a "password" credential in a {@code Map}. */
    String PASSWORD = "jio-password";
    /** This constant is provided an a convenience, to specify a "verification code" credential in a {@code Map}. */
    String VERIFICATION_CODE = "jio-verification-code";

    /**
     * Determine whether the user specified by the given unique identifier is authenticated.  Behavior when the user
     * doesn't exist is implementation-dependent.
     *
     * @param id The unique user identifier.
     *
     * @return Whether the user is authenticated.
     *
     * @throws AuthenticationException If there is an exception when determining authentication status.
     */
    boolean isAuthenticated(String id) throws AuthenticationException;

    /**
     * Determine whether the user specified by the given unique identifier exists.
     *
     * @param id The unique identifier of the user.
     *
     * @return Whether the user with the given identifier exists.
     *
     * @throws AuthenticationException If there is an exception when determining if the user exists/
     */
    boolean userExists(String id) throws AuthenticationException;

    /**
     * Determine whether the given credentials are valid for the user with the specified unique identifier.  Behavior
     * when the user doesn't exist is implementation-dependent.
     *
     * @param id The unique user identifier.
     * @param credentials A map of credentials containing key-value pairs, where the key is the <b>name</b> of
     * credential and the value is the <b>plaintext</b> credential.  Several constants that may be helpful key
     * choices are provided by this interface.
     *
     * @return Whether the given credentials are valid.
     *
     * @throws AuthenticationException If there is an exception when determining the validity of the credentials.
     */
    boolean credentialsValid(String id, Map<String, String> credentials) throws AuthenticationException;

    /**
     * Determine whether the given password is valid.  By default, this method assumes that the {@link #PASSWORD}
     * constant is the key used for storing passwords in the credentials Map.
     *
     * @param id The unique user identifier.
     * @param password The plaintext password.
     *
     * @return Whether the password is valid.
     *
     * @throws AuthenticationException If there is an exception when determining password validity.
     */
    default boolean passwordValid(String id, String password) throws AuthenticationException {
        return credentialsValid(id, Collections.singletonMap(PASSWORD, password));
    }

    /**
     * Authenticate the user with the given unique id and valid credentials and, if successful, populating the given
     * ExecutionContext with the user data from the backing data store.  By default, this method behaves in the
     * following fashion:
     * <pre>
     * {@code boolean canLogin =
     *       this.userExists(id) && !this.isAuthenticated(id) && this.credentialsValid(id,credentials);
     *   if (canLogin) {
     *       populate(context, id);
     *   }
     *   return canLogin;
     * }
     * </pre>
     *
     * @param context The context to be populated with user data if authentication is successful.  <b>This object
     * should not be modified if the authentication was unsuccessful.</b>
     * @param id The unique user identifier.
     * @param credentials A map of credentials containing key-value pairs, where the key is the <b>name</b> of
     * credential and the value is the <b>plaintext</b> credential.  Several constants that may be helpful key
     * choices are provided by this interface.
     *
     * @return Whether the user was successfully authenticated.  If the user was not successfully authenticated, it is
     * imperative that the {@code context} parameter not be modified.
     *
     * @throws AuthenticationException If there is an exception when authenticating the user.
     */
    default boolean authenticate(T context, String id, Map<String, String> credentials) throws AuthenticationException {
        boolean canLogin = this.userExists(id) && !this.isAuthenticated(id) && this.credentialsValid(id, credentials);
        if (canLogin) {
            populate(context, id);
        }
        return canLogin;
    }

    /**
     * Authenticate the user with the given unique id and valid password and, if successful, populating the given
     * ExecutionContext with the user data from the backing data store.  By default, this method assumes that the
     * {@link #PASSWORD} constant is the key used for storing passwords in the credentials Map.
     *
     * @param context The context to be populated with user data if authentication is successful.  <b>This object
     * should not be modified if the authentication was unsuccessful.</b>
     * @param id The unique user identifier.
     * @param password The plaintext password.
     *
     * @return Whether the user was successfully authenticated.  If the user was not successfully authenticated, it is
     * imperative that the {@code context} parameter not be modified.
     *
     * @throws AuthenticationException If there is an exception when authenticating the user.
     */
    default boolean authenticate(T context, String id, String password) throws AuthenticationException {
        return authenticate(context, id, Collections.singletonMap(PASSWORD, password));
    }

    /**
     * Unauthenticate the user with the given unique id, restoring the given context to an unauthenticated state.
     *
     * @param context The context for the user to be unauthenticated; should
     * @param id The unique user identifier.
     *
     * @return Whether the user was successfully unauthenticated and the context was modified.
     *
     * @throws AuthenticationException If there is an exception during unauthentication.
     */
    boolean unauthenticate(T context, String id) throws AuthenticationException;

    /**
     * Populate the given ExecutionContext with data for the user with the given unique identifier.
     * <p>
     * <blockquote>
     * <b>Note:</b> This method should not attempt any sort of authentication; credentials are not provided, it exists
     * for the sole purpose of retrieving and populating user data.  For authentication, use the {@link
     * #credentialsValid(String, Map)}, {@link #authenticate(ExecutionContext, String, Map)}, and {@link
     * #authenticate(ExecutionContext, String, String)} methods.
     * </blockquote>
     *
     * @param context The context to populate with user data.
     * @param id The unique user identifier.
     *
     * @throws AuthenticationException If there is an exception when populating the user data.
     */
    void populate(T context, String id) throws AuthenticationException;

    /**
     * Update the backing data store, for the user with the given id, with data from the given context.
     *
     * @param context The context containing data to be placed in the backing data store.
     * @param id The unique user id.
     *
     * @return {@code true} If the update was successful <b>and</b> the backing data store was modified.
     *
     * @throws AuthenticationException If there is an exception during the update.
     */
    boolean update(T context, String id) throws AuthenticationException;
}
