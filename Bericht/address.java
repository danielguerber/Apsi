	    if (address != null) {
	    	if (address.trim().isEmpty()) {
	    		errors.add("Keine Adresse.");
	    	} else if (!address.matches("[èéÈÉäöüÄÖÜß\\w\\s\\.\\-]+")) {
	    		errors.add("Ung&uuml;ltige Adresse.");
	    	}
	    }