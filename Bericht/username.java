	/**
	 * Creates a new Username with the name of the company as a base.
	 * @return new username
	 * @throws SQLException thrown on database error
	 */
	@CheckReturnValue
	private final String createUsername() throws SQLException {
		String usernameBase = name != null ? name.replace(" ", "") : "";
		int tries = 0;
		
		String newUsername;
		boolean collision;
		try (Connection con = ConnectionHandler.getConnection()) {
			do {
				newUsername = usernameBase + (tries > 0 ? tries : "");
				tries++;
			
				try (PreparedStatement stm = con.prepareStatement("SELECT `username` FROM `company` WHERE `username` = ? ")) {
					stm.setString(1, newUsername);
					collision = stm.executeQuery().next();
				}
			} while (collision);
			
			return newUsername;
		}
	}