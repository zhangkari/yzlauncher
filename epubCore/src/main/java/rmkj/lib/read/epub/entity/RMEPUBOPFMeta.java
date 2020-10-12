package rmkj.lib.read.epub.entity;

import java.util.HashMap;
import java.util.Map;

public class RMEPUBOPFMeta {
	private String title;
	private String creator;
	private String language;
	private String identifier;
	private String publisher;
	private String date;
	private String subject;
	private String description;
	private String contributor;
	private String type;
	private String format;
	private String source;
	private String relation;
	private String coverage;
	private String rights;
	private Map<String, String> meta = new HashMap<String, String>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public Map<String, String> getMeta() {
		return meta;
	}

	public void addMeta(String key, String value) {
		meta.put(key, value);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("title:" + title);
		sb.append("\n");
		sb.append("creator:" + creator);
		sb.append("\n");
		sb.append("language:" + language);
		sb.append("\n");
		sb.append("identifier:" + identifier);
		sb.append("\n");
		sb.append("publisher:" + publisher);
		sb.append("\n");
		sb.append("date:" + date);
		sb.append("\n");
		sb.append("subject:" + subject);
		sb.append("\n");
		sb.append("description:" + description);
		sb.append("\n");
		sb.append("contributor:" + contributor);
		sb.append("\n");
		sb.append("type:" + type);
		sb.append("\n");
		sb.append("format:" + format);
		sb.append("\n");
		sb.append("source:" + source);
		sb.append("\n");
		sb.append("format:" + format);
		sb.append("\n");
		sb.append("relation:" + relation);
		sb.append("\n");
		sb.append("coverage:" + coverage);
		sb.append("\n");
		sb.append("rights:" + rights);
		for (String key : meta.keySet()) {
			sb.append("\n");
			sb.append(key);
			sb.append(":");
			sb.append(meta.get(key));
		}
		return sb.toString();
	}
}
