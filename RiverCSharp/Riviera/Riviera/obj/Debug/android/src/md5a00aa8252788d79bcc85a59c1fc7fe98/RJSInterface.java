package md5a00aa8252788d79bcc85a59c1fc7fe98;


public class RJSInterface
	extends java.lang.Object
	implements
		mono.android.IGCUserPeer
{
/** @hide */
	public static final String __md_methods;
	static {
		__md_methods = 
			"n_ShowToast:(Ljava/lang/String;)V:__export__\n" +
			"n_ShowAlert:(Ljava/lang/String;Ljava/lang/String;)V:__export__\n" +
			"n_RegisterTask:(Ljava/lang/String;)Ljava/lang/String;:__export__\n" +
			"n_SyncWithRemote:(Ljava/lang/String;)Ljava/lang/String;:__export__\n" +
			"";
		mono.android.Runtime.register ("RivieraInterfaces.RJSInterface, Riviera, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null", RJSInterface.class, __md_methods);
	}


	public RJSInterface ()
	{
		super ();
		if (getClass () == RJSInterface.class)
			mono.android.TypeManager.Activate ("RivieraInterfaces.RJSInterface, Riviera, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null", "", this, new java.lang.Object[] {  });
	}

	public RJSInterface (android.content.Context p0)
	{
		super ();
		if (getClass () == RJSInterface.class)
			mono.android.TypeManager.Activate ("RivieraInterfaces.RJSInterface, Riviera, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null", "Android.Content.Context, Mono.Android, Version=0.0.0.0, Culture=neutral, PublicKeyToken=84e04ff9cfb79065", this, new java.lang.Object[] { p0 });
	}

	@android.webkit.JavascriptInterface

	public void ShowToast (java.lang.String p0)
	{
		n_ShowToast (p0);
	}

	private native void n_ShowToast (java.lang.String p0);

	@android.webkit.JavascriptInterface

	public void ShowAlert (java.lang.String p0, java.lang.String p1)
	{
		n_ShowAlert (p0, p1);
	}

	private native void n_ShowAlert (java.lang.String p0, java.lang.String p1);

	@android.webkit.JavascriptInterface

	public java.lang.String RegisterTask (java.lang.String p0)
	{
		return n_RegisterTask (p0);
	}

	private native java.lang.String n_RegisterTask (java.lang.String p0);

	@android.webkit.JavascriptInterface

	public java.lang.String SyncWithRemote (java.lang.String p0)
	{
		return n_SyncWithRemote (p0);
	}

	private native java.lang.String n_SyncWithRemote (java.lang.String p0);

	private java.util.ArrayList refList;
	public void monodroidAddReference (java.lang.Object obj)
	{
		if (refList == null)
			refList = new java.util.ArrayList ();
		refList.add (obj);
	}

	public void monodroidClearReferences ()
	{
		if (refList != null)
			refList.clear ();
	}
}
