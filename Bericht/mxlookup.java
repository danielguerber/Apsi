	/**
	 * Checks if a Mailserver is registered for the specified
	 *  email adress.
	 * @param mail email adress to check
	 * @return true if mailserver is registered
	 */
	@CheckReturnValue
	private static final boolean mxLookup(@Nonnull String mail) {
		String[] temp = mail.split("@");
		String hostname = temp[1];
		Hashtable<String, String> env = new Hashtable<String, String>();
		
		env.put("java.naming.factory.initial",
				"com.sun.jndi.dns.DnsContextFactory");
		try {
			DirContext ictx = new InitialDirContext(env);
			Attributes attrs = ictx.getAttributes(hostname, new String[] {"MX"});
			Attribute attr = attrs.get("MX");
			if (attr == null) {
				return false;
			} else {
				return true;
			}
		} catch (NamingException e) {
			return false;
		}
	}