	    if (mail != null && !mail.trim().isEmpty()) {
	        if (!mail.matches("//RFC-5322")) {
	            errors.add("Ung&uuml;ltige Email-Adresse.");
	        } else if(!mxLookup(mail)) {
	        	errors.add("Ung&uuml;ltige Email-Adresse.");
	        }
	    } else {
            errors.add("Keine Email-Adresse");
	    }