package com.invoice.binding;

public enum ReportTypeEnum
{

	PDF("PDF", ".pdf"), CSV("CSV", ".csv"), XLSX("XLSX", ".xlsx"), HTML("HTML", ".html"), XML("XML", ".xml"),
	DOC("DOC", ".doc");

	private final String code;
	private final String extension;

	public String getCode()
	{
		return code;
	}

	public String getExtension()
	{
		return extension;
	}

	ReportTypeEnum(String code, String extension)
	{
		this.code = code;
		this.extension = extension;
	}

	// ✅ method to get enum by string code
	public static ReportTypeEnum getReportTypeByCode(String code)
	{
		for (ReportTypeEnum type : values())
		{
			if (type.getCode().equalsIgnoreCase(code))
			{
				return type;
			}
		}
		return PDF; // default if not matched
	}
}
