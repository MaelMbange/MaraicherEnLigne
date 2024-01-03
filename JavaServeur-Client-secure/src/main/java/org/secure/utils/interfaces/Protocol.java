package org.secure.utils.interfaces;

import org.secure.utils.exceptions.EOCException;

public interface Protocol {
    String getName();
    Reponse analyse(Requete request) throws EOCException;
}