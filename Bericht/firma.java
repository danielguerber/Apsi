	    if (name != null) {
	    	if (name.trim().isEmpty()) {
	    		errors.add("Firmenname eingeben.");
	    	} else if (name.trim().length() > 20) {
	    		errors.add("Firmenname zu lang (max. 20 Zeichen).");
	    	} else if (!name.matches("([èéÈÉäöüÄÖÜßa-zA-Z\\s]+)")) {
	    		errors.add("Ung&uuml;ltige Zeichen im Firmennamen");
	    	}
	    }