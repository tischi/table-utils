package de.embl.cba.tables.github;

public class GitHubContentGetter
{
	private String repository;
	private String accessToken;
	private String path;

	/**
	 * https://developer.github.com/v3/repos/contents/
	 *
	 *
	 * @param repository
	 * @param path
	 */
	public GitHubContentGetter( String repository, String path )
	{
		this.repository = repository;
		this.path = path;
	}

	public GitHubContentGetter( String repository, String path, String accessToken )
	{
		this( repository, path );
		this.accessToken = accessToken;
	}

	public String getContent()
	{
		// GET /repos/:owner/:repo/contents/:path

		String url = createGetContentApiUrl( path );
		final String requestMethod = "GET";
		final RESTCaller restCaller = new RESTCaller();
		return restCaller.get( url, requestMethod, accessToken );
	}

	private String createGetContentApiUrl( String path )
	{
		String url = repository.replace( "github.com", "api.github.com/repos" );
		if ( ! url.endsWith( "/" ) ) url += "/";
		if ( ! path.startsWith( "/" ) ) path = "/" + path;
		url += "contents" + path;
		return url;
	}

	public static void main( String[] args )
	{
		final GitHubContentGetter contentGetter = new GitHubContentGetter( "https://github.com/constantinpape/autophagosomes-clem", "data/10spd/misc/bookmarks" );

		System.out.println( contentGetter.getContent() );
	}
}
