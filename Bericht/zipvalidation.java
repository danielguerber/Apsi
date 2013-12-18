	/**
	 * Validates the zip code with the post.ch service.
	 * @param zip zip to validate
	 * @return true if correct code
	 */
	@CheckReturnValue
	private static final boolean validatePlz(int zip) {
		URL url;
		HttpURLConnection conn;
		
		String line;
		try {
			url = new URL("http://www.post.ch/db/owa/pv_plz_pack/pr_check_data?p_language=de&p_nap="+zip+"&p_localita=&p_cantone=&p_tipo=luogo");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			String encoding = conn.getContentEncoding();
			InputStreamReader reader = new InputStreamReader(conn.getInputStream(), encoding == null ? "UTF-8" : encoding); 
			BufferedReader rd = new BufferedReader(reader);
			try {
				while ((line = rd.readLine()) != null) {
					if(line.contains("Keine PLZ gefunden"))
						return false;
				}
			} finally {
				rd.close();
			}
				
			
            return true;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return false;
	}