/*
* Copyright 2010 Srikanth Reddy Lingala  
* 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License. 
* You may obtain a copy of the License at 
* 
* http://www.apache.org/licenses/LICENSE-2.0 
* 
* Unless required by applicable law or agreed to in writing, 
* software distributed under the License is distributed on an "AS IS" BASIS, 
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
* See the License for the specific language governing permissions and 
* limitations under the License. 
*/

package rmkj.lib.rzp.util;

import rmkj.lib.rzp.core.RZPFile;


public class InternalRZPConstants {

	private static int mode = RZPFile.RZPMODE_RZP;
	
	public static void init(int modeType)
	{
		InternalRZPConstants.mode = modeType;
	}
	
	public static long LOCSIG()
	{
		switch( mode )
		{
		case RZPFile.RZPMODE_ZIP:
			return ZIP_LOCSIG;
		case RZPFile.RZPMODE_RZP:
		default:
			return RZP_LOCSIG;
		}
	}
	
	public static long EXTSIG()
	{
		switch( mode )
		{
		case RZPFile.RZPMODE_ZIP:
			return ZIP_EXTSIG;
		case RZPFile.RZPMODE_RZP:
		default:
			return RZP_EXTSIG;
		}
	}
	
	public static long CENSIG()
	{
		switch( mode )
		{
		case RZPFile.RZPMODE_ZIP:
			return ZIP_CENSIG;
		case RZPFile.RZPMODE_RZP:
		default:
			return RZP_CENSIG;
		}
	}
	
	public static long ENDSIG()
	{
		switch( mode )
		{
		case RZPFile.RZPMODE_ZIP:
			return ZIP_ENDSIG;
		case RZPFile.RZPMODE_RZP:
		default:
			return RZP_ENDSIG;
		}
	}
	
	public static long DIGSIG()
	{
		switch( mode )
		{
		case RZPFile.RZPMODE_ZIP:
			return ZIP_DIGSIG;
		case RZPFile.RZPMODE_RZP:
		default:
			return RZP_DIGSIG;
		}
	}
	
	public static long ARCEXTDATREC()
	{
		switch( mode )
		{
		case RZPFile.RZPMODE_ZIP:
			return ZIP_ARCEXTDATREC;
		case RZPFile.RZPMODE_RZP:
		default:
			return RZP_ARCEXTDATREC;
		}
	}
	
	public static long SPLITSIG()
	{
		switch( mode )
		{
		case RZPFile.RZPMODE_ZIP:
			return ZIP_SPLITSIG;
		case RZPFile.RZPMODE_RZP:
		default:
			return RZP_SPLITSIG;
		}
	}
	
	public static long ZIP64ENDCENDIRLOC()
	{
		switch( mode )
		{
		case RZPFile.RZPMODE_ZIP:
			return ZIP_ZIP64ENDCENDIRLOC;
		case RZPFile.RZPMODE_RZP:
		default:
			return RZP_ZIP64ENDCENDIRLOC;
		}
	}
	
	public static long ZIP64ENDCENDIRREC()
	{
		switch( mode )
		{
		case RZPFile.RZPMODE_ZIP:
			return ZIP_ZIP64ENDCENDIRREC;
		case RZPFile.RZPMODE_RZP:
		default:
			return RZP_ZIP64ENDCENDIRREC;
		}
	}
	
	public static long EXTRAFIELDZIP64LENGTH()
	{
		switch( mode )
		{
		case RZPFile.RZPMODE_ZIP:
			return ZIP_EXTRAFIELDZIP64LENGTH;
		case RZPFile.RZPMODE_RZP:
		default:
			return RZP_EXTRAFIELDZIP64LENGTH;
		}
	}
	
	public static long AESSIG()
	{
		switch( mode )
		{
		case RZPFile.RZPMODE_ZIP:
			return ZIP_AESSIG;
		case RZPFile.RZPMODE_RZP:
		default:
			return RZP_AESSIG;
		}
	}
	
	/*
     * Header signatures
     */
	// Whenever a new Signature is added here, make sure to add it
	// in Zip4jUtil.getAllHeaderSignatures()
//	public static long LOCSIG = 0x04034b50L;	// "PK\003\004"
//	public static long EXTSIG = 0x08074b50L;	// "PK\007\008"
//	public static long CENSIG = 0x02014b50L;	// "PK\001\002"
//	public static long ENDSIG = 0x06054b50L;	// "PK\005\006"
//	public static long DIGSIG = 0x05054b50L;
//	public static long ARCEXTDATREC = 0x08064b50L;
//	public static long SPLITSIG = 0x08074b50L;
//    public static long ZIP64ENDCENDIRLOC = 0x07064b50L;
//    public static long ZIP64ENDCENDIRREC = 0x06064b50;
//    public static int EXTRAFIELDZIP64LENGTH = 0x0001;
//    public static int AESSIG = 0x9901;
    
    
    //RZP 和 ZIP 差异就在FILE MAGIC
    
    //RZP MAGIC
    private static long RZP_LOCSIG = 0x04035250L;	// "PK\003\004"
    private static long RZP_EXTSIG = 0x08075250L;	// "PK\007\008"
    private static long RZP_CENSIG = 0x02015250L;	// "PK\001\002"
    public static long RZP_ENDSIG = 0x06055250L;	// "PK\005\006"
    private static long RZP_DIGSIG = 0x05055250L;
    private static long RZP_ARCEXTDATREC = 0x08065250L;
    private static long RZP_SPLITSIG = 0x08075250L;
    private static long RZP_ZIP64ENDCENDIRLOC = 0x07065250L;
    private static long RZP_ZIP64ENDCENDIRREC = 0x06065250L;
    private static int RZP_EXTRAFIELDZIP64LENGTH = 0x0001;
    private static int RZP_AESSIG = 0x9901;
    
    //ZIP MAGIC
    private static long ZIP_LOCSIG = 0x04034b50L;	// "PK\003\004"
    private static long ZIP_EXTSIG = 0x08074b50L;	// "PK\007\008"
    private static long ZIP_CENSIG = 0x02014b50L;	// "PK\001\002"
    public static long ZIP_ENDSIG = 0x06054b50L;	// "PK\005\006"
    private static long ZIP_DIGSIG = 0x05054b50L;
    private static long ZIP_ARCEXTDATREC = 0x08064b50L;
    private static long ZIP_SPLITSIG = 0x08074b50L;
    private static long ZIP_ZIP64ENDCENDIRLOC = 0x07064b50L;
    private static long ZIP_ZIP64ENDCENDIRREC = 0x06064b50L;
    private static int ZIP_EXTRAFIELDZIP64LENGTH = 0x0001;
    private static int ZIP_AESSIG = 0x9901;
    

    /*
     * Header sizes in bytes (including signatures)
     */
    public static final int LOCHDR = 30;	// LOC header size
    public static final int EXTHDR = 16;	// EXT header size
    public static final int CENHDR = 46;	// CEN header size
    public static final int ENDHDR = 22;	// END header size

    /*
     * Local file (LOC) header field offsets
     */
    public static final int LOCVER = 4;	// version needed to extract
    public static final int LOCFLG = 6;	// general purpose bit flag
    public static final int LOCHOW = 8;	// compression method
    public static final int LOCTIM = 10;	// modification time
    public static final int LOCCRC = 14;	// uncompressed file crc-32 value
    public static final int LOCSIZ = 18;	// compressed size
    public static final int LOCLEN = 22;	// uncompressed size
    public static final int LOCNAM = 26;	// filename length
    public static final int LOCEXT = 28;	// extra field length

    /*
     * Extra local (EXT) header field offsets
     */
    public static final int EXTCRC = 4;	// uncompressed file crc-32 value
    public static final int EXTSIZ = 8;	// compressed size
    public static final int EXTLEN = 12;	// uncompressed size

    /*
     * Central directory (CEN) header field offsets
     */
    public static final int CENVEM = 4;	// version made by
    public static final int CENVER = 6;	// version needed to extract
    public static final int CENFLG = 8;	// encrypt, decrypt flags
    public static final int CENHOW = 10;	// compression method
    public static final int CENTIM = 12;	// modification time
    public static final int CENCRC = 16;	// uncompressed file crc-32 value
    public static final int CENSIZ = 20;	// compressed size
    public static final int CENLEN = 24;	// uncompressed size
    public static final int CENNAM = 28;	// filename length
    public static final int CENEXT = 30;	// extra field length
    public static final int CENCOM = 32;	// comment length
    public static final int CENDSK = 34;	// disk number start
    public static final int CENATT = 36;	// internal file attributes
    public static final int CENATX = 38;	// external file attributes
    public static final int CENOFF = 42;	// LOC header offset

    /*
     * End of central directory (END) header field offsets
     */
    public static final int ENDSUB = 8;	// number of entries on this disk
    public static final int ENDTOT = 10;	// total number of entries
    public static final int ENDSIZ = 12;	// central directory size in bytes
    public static final int ENDOFF = 16;	// offset of first CEN header
    public static final int ENDCOM = 20;	// zip file comment length
    
    public static final int STD_DEC_HDR_SIZE = 12;
    
    //AES Constants
    public static final int AES_AUTH_LENGTH = 10;
    public static final int AES_BLOCK_SIZE = 16;
    
    public static final int MIN_SPLIT_LENGTH = 65536;
    
    public static final long ZIP_64_LIMIT = 4294967295L;
	
	public static String OFFSET_CENTRAL_DIR = "offsetCentralDir";
	
	public static final String VERSION = "1.0.0";
	
	public static final int MODE_ZIP = 1;
	
	public static final int MODE_UNZIP = 2;
	
	public static final String WRITE_MODE = "rw";
	
	public static final String READ_MODE = "r";
	
	public static final int BUFF_SIZE = 1024 * 4;
	
	public static final int FILE_MODE_NONE = 0;
	
	public static final int FILE_MODE_READ_ONLY = 1;
	
	public static final int FILE_MODE_HIDDEN = 2;
	
	public static final int FILE_MODE_ARCHIVE = 32;
	
	public static final int FILE_MODE_READ_ONLY_HIDDEN = 3;
	
	public static final int FILE_MODE_READ_ONLY_ARCHIVE = 33;
	
	public static final int FILE_MODE_HIDDEN_ARCHIVE = 34;
	
	public static final int FILE_MODE_READ_ONLY_HIDDEN_ARCHIVE = 35;
	
	public static final int FILE_MODE_SYSTEM = 38;
	
	public static final int FOLDER_MODE_NONE = 16;
	
	public static final int FOLDER_MODE_HIDDEN = 18;

	public static final int FOLDER_MODE_ARCHIVE = 48;
	
	public static final int FOLDER_MODE_HIDDEN_ARCHIVE = 50;
	
	// Update local file header constants
	// This value holds the number of bytes to skip from
	// the offset of start of local header
	public static final int UPDATE_LFH_CRC = 14;
	
	public static final int UPDATE_LFH_COMP_SIZE = 18;
	
	public static final int UPDATE_LFH_UNCOMP_SIZE = 22;
	
	public static final int LIST_TYPE_FILE = 1;
	
	public static final int LIST_TYPE_STRING = 2;
	
	public static final int UFT8_NAMES_FLAG = 1 << 11;
	
	public static final String CHARSET_UTF8 = "UTF8";
	
	public static final String CHARSET_CP850 = "Cp850";
	
	public static final String CHARSET_COMMENTS_DEFAULT = "windows-1254";
	
	public static final String CHARSET_DEFAULT = System.getProperty("file.encoding");
	
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	public static final String ZIP_FILE_SEPARATOR = "/";
	
	public static final String THREAD_NAME = "RZP";
	
	public static final int MAX_ALLOWED_ZIP_COMMENT_LENGTH = 0xFFFF;
}
