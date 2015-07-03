package com.vadkel.full.dns.server.common.utils.dns;
/**
 * 
 * @author eliott
 * sources : https://tools.ietf.org/html/rfc1034
 * 			 https://tools.ietf.org/html/rfc1035
 * 			 http://www.zytrax.com/books/dns/ch15/
 *
 */
public final class DNS {
	public static final int DEFAULT_PORT = 53;

	/**
	 * TYPE = value // meaning
	 */
	public static final int 
							TYPE_A = 1, // a host address
							TYPE_NS = 2, // an authoritative name server
							TYPE_MD = 3, // a mail destination (Obsolete - use MX)
							TYPE_MF = 4, // a mail forwarder (Obsolete - use MX)
							TYPE_CNAME = 5, // the canonical name for an alias
							TYPE_SOA = 6, // marks the start of a zone of authority
							TYPE_MB = 7, // a mailbox domain name (EXPERIMENTAL)
							TYPE_MG = 8, // a mail group member (EXPERIMENTAL)
							TYPE_MR = 9, // a mail rename domain name (EXPERIMENTAL)
							TYPE_NULL = 10, // a null RR (EXPERIMENTAL)
							TYPE_WKS = 11, // a well known service description
							TYPE_PTR = 12, // a domain name pointer
							TYPE_HINFO = 13, // host information
							TYPE_MINFO = 14, // mailbox or mail list information
							TYPE_MX = 15, // mail exchange
							TYPE_TXT = 16; // text strings
	
	/**
	 * QTYPE = value // meaning
	 */
	public static final int 
							QTYPE_AXFR = 252, // A request for a transfer of an entire zone
							QTYPE_MAILB = 253, // A request for mailbox-related records (MB, MG or MR)
							QTYPE_MAILA = 254, // A request for mail agent RRs (Obsolete - see MX)
							QTYPE_ANY = 255; // A request for all records

	/**
	 * QCLASS = value // meaning
	 */
	public static final int 
							CLASS_IN = 1, // the Internet
							CLASS_CS = 2, // the CSNET class (Obsolete - used only for examples in some obsolete RFCs)
							CLASS_CH = 3, // the CHAOS class
							CLASS_HS = 4, // Hesiod [Dyer 87]
							QCLASS_ANY = 255; // any class

	public static final int 
							SHIFT_RESPONSE_CODE = 0,
							SHIFT_RESERVED = 4,
							SHIFT_RECURSE_AVAILABLE = 7,
							SHIFT_RECURSE_PLEASE = 8, 
							SHIFT_TRUNCATED = 9,
							SHIFT_AUTHORITATIVE = 10, 
							SHIFT_OPCODE = 11,
							SHIFT_QUERY = 15;
	
	public static final int 
							OPCODE_QUERY = 0, 
							OPCODE_IQUERY = 1,
							OPCODE_STATUS = 2;
	
	/**
	 * Limit sizes
	 */
	public static final int 
							LIMIT_SIZE_LABEL = 63, // octets or less
							LIMIT_SIZE_NAME = 255, // 255 octets or less
							LIMIT_SIZE_TTL = 32, // positive values of a signed 32 bit number.
							LIMIT_SIZE_UDP_MESSAGE = 512; // 512 octets or less
			

	private static final String[] typeNames = { 
			"Address", 
			"NameServer",
			"MailDomain", 
			"MailForwarder", 
			"CanonicalName", 
			"StartOfAuthority",
			"MailBox", 
			"MailGroup", 
			"MailRename", 
			"Null", 
			"WellKnownServices",
			"Pointer", 
			"HostInfo", 
			"MailInfo", 
			"MailExchanger", 
			"Text" 
	};

	public static String typeName(int type) {
		return ((type >= 1) && (type <= 16)) ? typeNames[type - 1] : "Unknown";
	}

	private static final String[] codeNames = { 
			"Format error",
			"Server failure", 
			"Name not known", 
			"Not implemented", 
			"Refused" 
	};

	public static String codeName(int code) {
		return ((code >= 1) && (code <= 5)) ? codeNames[code - 1]
				: "Unknown error";
	}
}
