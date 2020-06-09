/* Copyright 2010,2014 Bank Of Italy
*
* Licensed under the EUPL, Version 1.1 or - as soon they
* will be approved by the European Commission - subsequent
* versions of the EUPL (the "Licence");
* You may not use this work except in compliance with the
* Licence.
* You may obtain a copy of the Licence at:
*
*
* http://ec.europa.eu/idabc/eupl
*
* Unless required by applicable law or agreed to in
* writing, software distributed under the Licence is
* distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
* express or implied.
* See the Licence for the specific language governing
* permissions and limitations under the Licence.
*/
package it.bancaditalia.oss.sdmx.client;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.bancaditalia.oss.sdmx.api.GenericSDMXClient;
import it.bancaditalia.oss.sdmx.client.custom.FILE;
import it.bancaditalia.oss.sdmx.exceptions.SdmxException;
import it.bancaditalia.oss.sdmx.exceptions.SdmxExceptionFactory;
import it.bancaditalia.oss.sdmx.exceptions.SdmxInvalidParameterException;
import it.bancaditalia.oss.sdmx.exceptions.SdmxUnknownProviderException;
import it.bancaditalia.oss.sdmx.util.Configuration;
import it.bancaditalia.oss.sdmx.util.SdmxProxySelector;

/**
 * <p>Java Factory class for creating the Sdmx Clients.
 *
 * @author Attilio Mattiocco
 *
 */
public class SDMXClientFactory {

	private static final String ECB_PROVIDER = "https://sdw-wsrest.ecb.europa.eu/service";
	//private static final String ISTAT_PROVIDER = "http://sdmx.istat.it/SDMXWS/rest";
	private static final String ISTAT_PROVIDER_POP = "http://sdmx.istat.it/WS_CENSPOP/rest";
	private static final String ISTAT_PROVIDER_AGR = "http://sdmx.istat.it/WS_CENSAGR/rest";
	private static final String ISTAT_PROVIDER_IND = "http://sdmx.istat.it/WS_CIS/rest";
	private static final String INSEE_PROVIDER = "https://bdm.insee.fr/series/sdmx";
	private static final String UNDATA_PROVIDER = "http://data.un.org/WS/rest";
	private static final String WITS_PROVIDER = "http://wits.worldbank.org/API/V1/SDMX/V21/rest";
	//private static final String INEGI_PROVIDER = "http://sdmx.snieg.mx/service/Rest";
	private static final String IMF_SDMX_CENTRAL_PROVIDER = "https://sdmxcentral.imf.org/ws/public/sdmxapi/rest";
	//private static final String WB_PROVIDER = "https://api.worldbank.org/v2/sdmx/rest";
	private static final String ILO_PROVIDER = "https://www.ilo.org/sdmx/rest";
	private static final String ABS21_PROVIDER = "http://nsi-stable-siscc.redpelicans.com/rest";

	//read the configuration file
	static {
		providers = new TreeMap<>();
		logger = Configuration.getSdmxLogger();
		try {
			initBuiltInProviders();
			initExternalProviders();
		} catch (SdmxException e) {
			e.printStackTrace();
		}
	}

	private static final String sourceClass = SDMXClientFactory.class.getSimpleName();
	protected static Logger logger;
	private static NavigableMap<String, Provider> providers;


	/**
     * Initialize the internal sdmx providers
	 * @throws SdmxException 
     *
     */
	private static void initBuiltInProviders() throws SdmxException{
        addBuiltInProvider("ECB", ECB_PROVIDER, false, false, true, "European Central Bank", false);
        //addBuiltInProvider("EUROSTAT", EUROSTAT_PROVIDER, false, false, false, "Eurostat", false);
        addBuiltInProvider("ISTAT_CENSUS_POP", ISTAT_PROVIDER_POP, false, false, false, "ISTAT - Population and housing census 2011", false);
        addBuiltInProvider("ISTAT_CENSUS_AGR", ISTAT_PROVIDER_AGR, false, false, false, "ISTAT - Agricultural census 2010", false);
        addBuiltInProvider("ISTAT_CENSUS_IND", ISTAT_PROVIDER_IND, false, false, false, "ISTAT - Industry and services census 2011", false);
        addBuiltInProvider("INSEE", INSEE_PROVIDER, false, false, true, "National Institute of Statistics and Economic Studies", false);
        addBuiltInProvider("UNDATA", UNDATA_PROVIDER, false, false, false, "Data access system to UN databases", false);
        addBuiltInProvider("WITS", WITS_PROVIDER, false, false, false, "World Integrated Trade Solutions", false);
        //addBuiltInProvider("INEGI", INEGI_PROVIDER, false, false, false, "Instituto Nacional de Estadistica y Geografia", false);
        addBuiltInProvider("IMF_SDMX_CENTRAL", IMF_SDMX_CENTRAL_PROVIDER, false, false, true, "International Monetary Fund SDMX Central", false);
	    //addBuiltInProvider("WB", WB_PROVIDER, false, false, false, "World Bank - World Development Indicators", false);
	    addBuiltInProvider("ILO", ILO_PROVIDER, false, false, false, "International Labour Organization", false);
  		addBuiltInProvider("ABS2", ABS21_PROVIDER, false, false, false, "Australian Bureau of Statistics SDMX 2.1 (experimental)", false);

	    //add internal 2.0 providers
        addBuiltInProvider("ISTAT", null, false, false, false, "Italian National Institute of Statistics ", true);
	    addBuiltInProvider("OECD", null, false, false, false, "The Organisation for Economic Co-operation and Development", true);
	    addBuiltInProvider("StatsEE", null, false, false, false, "Statistics Estonia (BETA)", true);
	    addBuiltInProvider("OECD_RESTR", null, true, false, false, "The Organisation for Economic Co-operation and Development, RESTRICTED ACCESS", true);
	    addBuiltInProvider("ILO_Legacy", null, false, false, false, "International Labour Organization - Old Endpoint", true);
	    addBuiltInProvider("IMF2", null, false, false, false, "New International Monetary Fund endpoint", true);
	    addBuiltInProvider("ABS", null, false, false, false, "Australian Bureau of Statistics", true);
	    addBuiltInProvider("NBB", null, false, false, false, "National Bank Belgium", true);
	    addBuiltInProvider("UIS", null, false, false, false, "Unesco Institute for Statistics", true);
	    addBuiltInProvider("WB", null, false, false, false, "World Bank - World Development Indicators", true);
	    addBuiltInProvider("INEGI", null, false, false, false, "Instituto Nacional de Estadistica y Geografia", true);
	    addBuiltInProvider("EUROSTAT", null, false, false, false, "Eurostat", true);

	}
	
	/**
     * Initialize the sdmx providers from the configuration file
	 * @throws SdmxException 
     */
	private static void initExternalProviders() throws SdmxException{
	    //external providers set in the configuration file
	    String external = Configuration.getExternalProviders();
	    if(external != null && !external.isEmpty()){
	    	String[] ids = external.trim().split("\\s*,\\s*");
	    	for (int i = 0; i < ids.length; i++) {
				addExternalProvider(ids[i]);
			}
	    }
	}

	/**
     * General method for creating an SdmxClient.
     *
	 * @param name The name of the provider to create.
	 * @param endpoint the {@link URI} of the provider to create.
	 * @param needsCredentials true if the provider needs authentication.
	 * @param needsURLEncoding true if the provider needs the URL to be encoded.
	 * @param supportsCompression true if the provider supports HTTP compression features.
	 * @param description The description of the provider
	 * @param isCustom true if the provider has an implementing class in the package it.bancaditalia.oss.sdmx.client.custom
	 * 
	 * @throws SdmxException if there is an error creating the provider. 
	 */
	public static void addProvider(String name, URI endpoint, boolean needsCredentials, boolean needsURLEncoding, boolean supportsCompression, String description, boolean isCustom) throws SdmxException{
		Provider p = new Provider(name, endpoint, null, needsCredentials, needsURLEncoding, supportsCompression, description, isCustom);
    	providers.put(name, p);
	}

	/**
     * General method for creating an SdmxClient.
     *
	 * @param name The name of the provider to create.
	 * @param endpoint the {@link URI} of the provider to create.
	 * @param trustStore A truststore to use to connect to this provider.
	 * @param needsCredentials true if the provider needs authentication.
	 * @param needsURLEncoding true if the provider needs the URL to be encoded.
	 * @param supportsCompression true if the provider supports HTTP compression features.
	 * @param description The description of the provider
	 * @param isCustom true if the provider has an implementing class in the package it.bancaditalia.oss.sdmx.client.custom
	 * 
	 * @throws SdmxException if there is an error creating the provider. 
	 */
	public static void addProvider(String name, URI endpoint, KeyStore trustStore, boolean needsCredentials, boolean needsURLEncoding, boolean supportsCompression, String description, boolean isCustom) throws SdmxException{
		Provider p = new Provider(name, endpoint, trustStore, needsCredentials, needsURLEncoding, supportsCompression, description, isCustom);
    	providers.put(name, p);
	}

    /**
     * Add a builtin provider and check whether the default values need to be overwritten with values defined in the configuration file.
     * @throws SdmxException 
     */
    private static void addBuiltInProvider(final String name, final String endpoint, final Boolean needsCredentials, final Boolean needsURLEncoding, final Boolean supportsCompression, final String description, boolean isCustom) throws SdmxException {
        try {
            final String providerName = Configuration.getConfiguration().getProperty("providers." + name + ".name", name);
            final String providerEndpoint = Configuration.getConfiguration().getProperty("providers." + name + ".endpoint", endpoint);
            final URI providerURL = null != providerEndpoint ? new URI(providerEndpoint) : null;
            final boolean provdiderNeedsCredentials = Boolean.parseBoolean(Configuration.getConfiguration().getProperty("providers." + name + ".needsCredentials", needsCredentials.toString()));
            final boolean providerNeedsURLEncoding = Boolean.parseBoolean(Configuration.getConfiguration().getProperty("providers." + name + ".needsURLEncoding", needsURLEncoding.toString()));
            final boolean providerSupportsCompression = Boolean.parseBoolean(Configuration.getConfiguration().getProperty("providers." + name + ".supportsCompression", supportsCompression.toString()));
            final String providerDescription = Configuration.getConfiguration().getProperty("providers." + name + ".description", description);
            addProvider(providerName, providerURL, null, provdiderNeedsCredentials, providerNeedsURLEncoding, providerSupportsCompression, providerDescription, isCustom);
        } catch (URISyntaxException e) {
            logger.log(Level.SEVERE, "Exception. Class: {0} .Message: {1}", new Object[]{e.getClass().getName(), e.getMessage()});
            logger.log(Level.FINER, "", e);
		}
    }

    /**
     * Add a external provider and check whether the default values need to be overwritten with values defined in the configuration file.
     * @throws SdmxException 
     */
    private static void addExternalProvider(final String id) throws SdmxException {
        try {
            final String providerName = Configuration.getConfiguration().getProperty("providers." + id + ".name", id);
            final String providerEndpoint = Configuration.getConfiguration().getProperty("providers." + id + ".endpoint");
            if(providerEndpoint != null && !providerEndpoint.isEmpty()){
            	final URI providerURL = new URI(providerEndpoint);
		        final boolean provdiderNeedsCredentials = Boolean.parseBoolean(Configuration.getConfiguration().getProperty("providers." + id + ".needsCredentials", "false"));
		        final boolean providerNeedsURLEncoding = Boolean.parseBoolean(Configuration.getConfiguration().getProperty("providers." + id + ".needsURLEncoding", "false"));
		        final boolean providerSupportsCompression = Boolean.parseBoolean(Configuration.getConfiguration().getProperty("providers." + id + ".supportsCompression", "false"));
		        final String providerDescription = Configuration.getConfiguration().getProperty("providers." + id + ".description", id);
		        
		        String trustStoreLocation = Configuration.getConfiguration().getProperty("providers." + id + ".trustStore", "");
		        KeyStore providerTrustStore = null;
		        if (!"".equals(trustStoreLocation))
					try {
				        InputStream trustStoreFile = new FileInputStream(new File(trustStoreLocation));
						providerTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
						providerTrustStore.load(trustStoreFile, "changeit".toCharArray());
					} catch (FileNotFoundException e) {
						logger.warning("Cannot open trust store at " + trustStoreLocation);
					} catch (GeneralSecurityException e) {
						e.printStackTrace();
					} catch (IOException e) {
						throw SdmxExceptionFactory.wrap(e);
					} finally {
						providerTrustStore = null;
					}
		        
		        addProvider(providerName, providerURL, providerTrustStore, provdiderNeedsCredentials, providerNeedsURLEncoding, providerSupportsCompression, providerDescription, false);
            }
            else{
            	logger.warning("No URL has been configured for the external provider: '" + id + "'. It will be skipped.");
            	return;
            }
        } catch (URISyntaxException e) {
            logger.log(Level.SEVERE, "Exception. Class: {0} .Message: {1}", new Object[]{e.getClass().getName(), e.getMessage()});
            logger.log(Level.FINER, "", e);
		}
    }

	/**
     * General method for creating an SdmxClient.
     *
	 * @param providerName A non-null provider identification short name. 
	 * @return The client.
	 * @throws SdmxException if there is an error creating the client.
	 */
	public static GenericSDMXClient createClient(String providerName) throws SdmxException {
		final String sourceMethod = "createClient";

		logger.entering(sourceClass, sourceMethod);
		logger.fine("Create an SDMX client for '" + providerName + "'");
		GenericSDMXClient client = null;
		Provider provider = providers.get(providerName);
		if(provider == null){
			throw new SdmxInvalidParameterException("The provider '" + providerName + "' is not available in this configuration.");
		}
		String hostname = null;

		if(provider != null && !provider.isCustom())
		{
			hostname = provider.getEndpoint().getHost();
			if(provider.getEndpoint().getScheme().toLowerCase().startsWith("http")){
				client = new RestSdmxClient(provider.getName(), provider.getEndpoint(), provider.getSSLSocketFactory(), provider.isNeedsCredentials(), provider.isNeedsURLEncoding(), provider.isSupportsCompression());
			}
			else if(provider.getEndpoint().getScheme().toLowerCase().equals("file")){
				client = new FILE(provider.getName(), provider.getEndpoint());
			}
			else 
			{
				throw new SdmxInvalidParameterException("The protocol '" + provider.getEndpoint().getScheme() + "' is not supported.");
			}
		}
		else {
			///legacy 2.0
			try {
				Class<?> clazz = Class.forName("it.bancaditalia.oss.sdmx.client.custom." + providerName);
				client = (GenericSDMXClient)clazz.newInstance();
				// apply customizations eventually added by user in configuration file
				// for now only endpoint can be overridden
				if (provider.getEndpoint() != null)
					client.setEndpoint(provider.getEndpoint());

				if (client.getEndpoint() != null)
					hostname = client.getEndpoint().getHost();
				
				client.setName(providerName);
			}
			catch (ClassNotFoundException e) {
				logger.severe("The provider '" + providerName + "' is not available in this configuration.");
				throw new SdmxUnknownProviderException(providerName, e);
			} catch (IllegalAccessException e) {
				logger.severe("The provider implementation it.bancaditalia.oss.sdmx.client.custom." + providerName + " does not define a default constructor.");
				throw new SdmxUnknownProviderException(providerName, e);
			} catch (InstantiationException e) {
				logger.severe("Could not instantiate provider implementation it.bancaditalia.oss.sdmx.client.custom." + providerName);
				throw new SdmxUnknownProviderException(providerName, e);
			}
		}

		// now set default proxy if necessary
    	ProxySelector ps = ProxySelector.getDefault();
		if (hostname != null && ps != null && ps instanceof SdmxProxySelector)
	    	((SdmxProxySelector)ps).addToDefaultProxy(hostname);

		logger.exiting(sourceClass, sourceMethod);
		return client;
	}

	/**
	 * Get the list of all available SDMX Providers
	 * @return A map of providers with keys as names and {@link Provider} instances as values.
	 */
	public static NavigableMap<String, Provider> getProviders() {
        return providers;
    }
}
