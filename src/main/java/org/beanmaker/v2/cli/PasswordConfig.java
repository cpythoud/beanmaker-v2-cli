package org.beanmaker.v2.cli;

import org.jcodegen.html.xmlbase.XMLElement;

import java.util.Arrays;

class PasswordConfig {

    private final boolean promptOnly;
    private final char[] password;
    private final boolean encrypted;

    private PasswordConfig(boolean promptOnly, char[] password, boolean encrypted) {
        this.promptOnly = promptOnly;
        this.password = password;
        this.encrypted = encrypted;
    }

    static PasswordConfig promptOnly() {
        return new PasswordConfig(true, null, false);
    }

    static PasswordConfig clearText(char[] password) {
        return new PasswordConfig(false, password, false);
    }

    static PasswordConfig encrypted(char[] password) {
        return new PasswordConfig(false, password, true);
    }

    static PasswordConfig encrypted(char[] password, char[] passphrase) {
        // TODO: encrypt password with passphrase
        throw new UnsupportedOperationException();
    }

    static PasswordConfig fromCommandOptions(Console out, char[] cleartextPassword, char[] password, char[] passphrase) {
        PasswordConfig passwordConfig;
        if (cleartextPassword == null && password == null) {
            passwordConfig = promptOnly();
            if (passphrase != null)
                out.println(Status.NOTICE, "--passphrase option is not used by the command. Interactive mode selected by default.");
        } else {
            if (cleartextPassword != null) {
                passwordConfig = clearText(cleartextPassword);
                if (passphrase != null)
                    out.println(Status.NOTICE, "--passphrase option is not used by the command, since you choosed to store the password in clear text.");
            } else {
                if (passphrase == null)
                    passwordConfig = encrypted(password);
                else
                    passwordConfig = encrypted(password, passphrase);
            }
        }
        return passwordConfig;
    }

    XMLElement getXMLElement() {
        var passwordElement = new XMLElement("password");
        if (promptOnly)
            passwordElement.addChild(ConfigData.createXMLElement("interactive"));
        else {
            if (encrypted)
                passwordElement.addChild(ConfigData.createXMLElement("encrypted", password));
            else
                passwordElement.addChild(ConfigData.createXMLElement("cleartext", password));
        }
        return passwordElement;
    }

    @Override
    public int hashCode() {
        int hashCode = Boolean.hashCode(promptOnly);
        hashCode = 31 * hashCode + Arrays.hashCode(password);
        hashCode = 31 * hashCode + Boolean.hashCode(encrypted);
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj instanceof PasswordConfig passwordConfig)
            return promptOnly == passwordConfig.promptOnly
                    && encrypted == passwordConfig.encrypted
                    && Arrays.equals(password, passwordConfig.password);

        return false;
    }

}
