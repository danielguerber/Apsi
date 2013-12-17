	/**
	 * Validates the given password.
	 * @param pw password
	 * @return true if password is valid
	 */
	@CheckReturnValue
	public static final String validatePassword(@CheckForNull String pw) {
		String error = null;
		if (pw != null) {
			if (pw.trim().length() < 8) {
				error = "Passwort zu kurz (min. 8 Zeichen).";
			} else if (pw.trim().length() > 64) {
				error ="Passwort zu lang (max. 64 Zeichen).";
			} else if (!pw.matches("[èéÈÉäöüÄÖÜß\\-\\_\\.\\w]+")) {
	    		error = "Ungültige Zeichen im Passwort.";
	    	}
		} else {
			error = "Passwort zu kurz (min. 8 Zeichen).";
		}
		return error;
	}