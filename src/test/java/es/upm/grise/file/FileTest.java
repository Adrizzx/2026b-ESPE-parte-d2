package es.upm.grise.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.upm.grise.file.exceptions.InvalidContentException;
import es.upm.grise.file.exceptions.WrongEncodingException;
import es.upm.grise.file.exceptions.WrongFileTypeException;

class FileTest {

	private Location location;

	@BeforeEach
	public void setUp() {

		Path path = Paths.get("test.txt");
		location = new Location(path);

	}

	/*
	 * Constructor
	 */

	@Test
	public void testConstructor_ContentIsEmptyNotNull() {

		File file = new File(FileType.PROPERTY, location);

		assertNotNull(file.getContent());
		assertTrue(file.getContent().isEmpty());

	}

	@Test
	public void testConstructor_TypeIsAssigned() {

		File file = new File(FileType.PROPERTY, location);

		assertEquals(FileType.PROPERTY, file.getType());

	}

	@Test
	public void testConstructor_LocationIsAssigned() {

		File file = new File(FileType.IMAGE, location);

		assertEquals(location, file.getLocation());

	}

	/*
	 * addProperty(char[] content)
	 */

	@Test
	public void testAddProperty_ValidKeyValue_AddsContent() throws InvalidContentException, WrongFileTypeException {

		File file = new File(FileType.PROPERTY, location);
		char[] content = "DATE=20250919".toCharArray();

		file.addProperty(content);

		assertEquals(content.length, file.getContent().size());
		for (int i = 0; i < content.length; i++) {
			assertEquals(content[i], file.getContent().get(i));
		}

	}

	@Test
	public void testAddProperty_AppendsToExistingContent() throws InvalidContentException, WrongFileTypeException {

		File file = new File(FileType.PROPERTY, location);
		file.addProperty("A=1".toCharArray());
		file.addProperty("B=2".toCharArray());

		assertEquals("A=1B=2", contentAsString(file));

	}

	@Test
	public void testAddProperty_NullContent_ThrowsInvalidContentException() {

		File file = new File(FileType.PROPERTY, location);

		assertThrows(InvalidContentException.class, () -> file.addProperty(null));

	}

	@Test
	public void testAddProperty_ImageType_ThrowsWrongFileTypeException() {

		File file = new File(FileType.IMAGE, location);

		assertThrows(WrongFileTypeException.class, () -> file.addProperty("DATE=20250919".toCharArray()));

	}

	@Test
	public void testAddProperty_EmptyContent_ThrowsInvalidContentException() {

		File file = new File(FileType.PROPERTY, location);

		assertThrows(InvalidContentException.class, () -> file.addProperty(new char[0]));

	}

	@Test
	public void testAddProperty_NoEqualsSign_ThrowsInvalidContentException() {

		File file = new File(FileType.PROPERTY, location);

		assertThrows(InvalidContentException.class, () -> file.addProperty("DATE20250919".toCharArray()));

	}

	@Test
	public void testAddProperty_MultipleEqualsSigns_ThrowsInvalidContentException() {

		File file = new File(FileType.PROPERTY, location);

		assertThrows(InvalidContentException.class, () -> file.addProperty("DATE=2025=0919".toCharArray()));

	}

	@Test
	public void testAddProperty_EqualsAtStart_ThrowsInvalidContentException() {

		File file = new File(FileType.PROPERTY, location);

		assertThrows(InvalidContentException.class, () -> file.addProperty("=20250919".toCharArray()));

	}

	@Test
	public void testAddProperty_EqualsAtEnd_ThrowsInvalidContentException() {

		File file = new File(FileType.PROPERTY, location);

		assertThrows(InvalidContentException.class, () -> file.addProperty("DATE=".toCharArray()));

	}

	/*
	 * addImageBytes(char[] content)
	 */

	@Test
	public void testAddImageBytes_ValidContent_AddsContent()
			throws InvalidContentException, WrongFileTypeException, WrongEncodingException {

		File file = new File(FileType.IMAGE, location);
		char[] content = new char[] { 0, 128, 255 };

		file.addImageBytes(content);

		assertEquals(content.length, file.getContent().size());
		for (int i = 0; i < content.length; i++) {
			assertEquals(content[i], file.getContent().get(i));
		}

	}

	@Test
	public void testAddImageBytes_AppendsToExistingContent()
			throws InvalidContentException, WrongFileTypeException, WrongEncodingException {

		File file = new File(FileType.IMAGE, location);
		file.addImageBytes(new char[] { 1, 2 });
		file.addImageBytes(new char[] { 3, 4 });

		assertEquals(4, file.getContent().size());
		assertEquals((char) 1, file.getContent().get(0));
		assertEquals((char) 2, file.getContent().get(1));
		assertEquals((char) 3, file.getContent().get(2));
		assertEquals((char) 4, file.getContent().get(3));

	}

	@Test
	public void testAddImageBytes_NullContent_ThrowsInvalidContentException() {

		File file = new File(FileType.IMAGE, location);

		assertThrows(InvalidContentException.class, () -> file.addImageBytes(null));

	}

	@Test
	public void testAddImageBytes_PropertyType_ThrowsWrongFileTypeException() {

		File file = new File(FileType.PROPERTY, location);

		assertThrows(WrongFileTypeException.class, () -> file.addImageBytes(new char[] { 0, 1, 2 }));

	}

	@Test
	public void testAddImageBytes_CharGreaterThan255_ThrowsWrongEncodingException() {

		File file = new File(FileType.IMAGE, location);

		assertThrows(WrongEncodingException.class, () -> file.addImageBytes(new char[] { 256 }));

	}

	@Test
	public void testAddImageBytes_CharAt255_IsValidBoundary()
			throws InvalidContentException, WrongFileTypeException, WrongEncodingException {

		File file = new File(FileType.IMAGE, location);

		file.addImageBytes(new char[] { 255 });

		assertEquals(1, file.getContent().size());
		assertEquals((char) 255, file.getContent().get(0));

	}

	/*
	 * removeContent(int numberChars)
	 */

	@Test
	public void testRemoveContent_RemovesLastNChars() throws InvalidContentException, WrongFileTypeException {

		File file = new File(FileType.PROPERTY, location);
		file.addProperty("AB=CDE".toCharArray());

		file.removeContent(2);

		assertEquals("AB=C", contentAsString(file));

	}

	@Test
	public void testRemoveContent_ZeroChars_LeavesContentUnchanged()
			throws InvalidContentException, WrongFileTypeException {

		File file = new File(FileType.PROPERTY, location);
		file.addProperty("A=1".toCharArray());

		file.removeContent(0);

		assertEquals("A=1", contentAsString(file));

	}

	@Test
	public void testRemoveContent_NumberCharsEqualsSize_ClearsContent()
			throws InvalidContentException, WrongFileTypeException {

		File file = new File(FileType.PROPERTY, location);
		file.addProperty("A=1".toCharArray());

		file.removeContent(file.getContent().size());

		assertTrue(file.getContent().isEmpty());

	}

	@Test
	public void testRemoveContent_NumberCharsGreaterThanSize_ClearsContentWithoutException()
			throws InvalidContentException, WrongFileTypeException {

		File file = new File(FileType.PROPERTY, location);
		file.addProperty("A=1".toCharArray());

		file.removeContent(100);

		assertTrue(file.getContent().isEmpty());

	}

	@Test
	public void testRemoveContent_OnEmptyContent_NoExceptionAndStaysEmpty() {

		File file = new File(FileType.PROPERTY, location);

		file.removeContent(5);

		assertTrue(file.getContent().isEmpty());

	}

	/*
	 * helper
	 */

	private String contentAsString(File file) {

		StringBuilder sb = new StringBuilder();
		for (char c : file.getContent()) {
			sb.append(c);
		}
		return sb.toString();

	}

}
