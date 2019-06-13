package poc.fwk.logger.test;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import poc.fwk.logger.annotations.Logger;
import poc.fwk.logger.test.entities.PojoEntity;
import poc.fwk.logger.test.entities.PojoEntityElement;
import poc.fwk.logger.test.entities.PojoEntityEntry;

@Component
@Logger
@RequiredArgsConstructor
public class LoggerTestComponent {

	public PojoEntity returnValue() {
		PojoEntity pojoEntity = new PojoEntity();
		pojoEntity.setId(1);

		PojoEntityElement element = new PojoEntityElement();
		element.setId(2);
		element.setValue("element");
		pojoEntity.setElement(element);

		PojoEntityEntry entry = new PojoEntityEntry();
		entry.setId(3);
		entry.setValue("entry");
		pojoEntity.setEntries(Arrays.asList(entry));

		return pojoEntity;
	}

	public void returnVoid(PojoEntity entity) {

	}

	public InputStream returnInputStream() {
		return Mockito.mock(InputStream.class);
	}

	public OutputStream returnOutputStream() {
		return Mockito.mock(OutputStream.class);
	}

	public byte[] returnByteArray() {
		return new byte[] { 1, 2, 3, 4, 5 };
	}

	public ResponseEntity<String> returnValueResponse() {
		return new ResponseEntity<>(new String(), HttpStatus.OK);
	}

	public ResponseEntity<byte[]> returnByteArrayResponse() {
		return new ResponseEntity<>(new byte[] { 1, 2, 3, 4, 5 }, HttpStatus.OK);
	}
}
