try (Connection con = ConnectionHandler.getConnection()) {
	try (PreparedStatement stm = 
			con.prepareStatement("UPDATE `company` SET `password`= ?" 
				+ "WHERE `username`= ? AND `password` = ?")) {
		stm.setString(1, hash(username, newPassword));
		stm.setString(2, username);
		stm.setString(3, hash(username, oldPassword));
		
		stm.execute();
		return stm.getUpdateCount() > 0;
	}
} 