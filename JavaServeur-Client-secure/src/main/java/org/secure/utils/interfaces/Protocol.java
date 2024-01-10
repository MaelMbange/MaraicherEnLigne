package org.secure.utils.interfaces;

import org.secure.serveur.ContainerPublicKey;
import org.secure.utils.exceptions.EOCException;

import javax.crypto.SecretKey;
import java.security.PublicKey;

public interface Protocol {
    String getName();
    Reponse analyse(Requete request) throws EOCException;
    Reponse analyse(Requete request, SecretKey cleSession, ContainerPublicKey clePublicContainer) throws EOCException;
}