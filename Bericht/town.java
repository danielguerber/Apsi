	    if (town != null) { 
	    	if (town.trim().isEmpty()) {
	    		errors.add("Keine Stadt.");
	    	} else if (!town.matches("[èéÈÉäöüÄÖÜßa-zA-Z\\-\\.\\s]+")) {
	    		errors.add("Ung&uuml;ltige Stadt.");
	    	}
	    }