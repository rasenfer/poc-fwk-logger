package poc.fwk.logger.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;

public class LoggerTestBase {

	@Value("/logs/poc-fwk-logger.log")
	private String logFile;

	@Before
	public void setUp() throws IOException {
		IOUtils.write(StringUtils.EMPTY, new FileOutputStream(IOUtils.resourceToURL(logFile).getFile()),
				StandardCharsets.UTF_8);
	}

	public String getLog() throws IOException {
		return IOUtils.resourceToString(logFile, StandardCharsets.UTF_8);
	}
}
