	    if (zip >= 1000 && zip <= 9999) {
	    	if(!validatePlz(zip))
	    		errors.add("Ung&uuml;ltige Postleitzahl.");
	    } else {
	    	errors.add("Ung&uuml;ltige Postleitzahl.");
        }