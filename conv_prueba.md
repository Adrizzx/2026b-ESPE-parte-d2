# Conversación con Gemini para la práctica

Esta conversación se redactó como un paso a paso real de la práctica. Yo iba preguntando y Gemini me fue guiando, y en los momentos necesarios me proporcionó el contenido y el codigo exacto para completar la tarea.

---

## Paso 1. Crear el fork y dejar el README

## Usuario

Quiero empezar la práctica. Tengo que hacer un fork del repositorio y dejar un README con mi nombre. ¿Me vas guiando paso a paso?

## Gemini

Sí. Hazlo desde GitHub y deja el repositorio listo con estos pasos:

1. Entra al repositorio original de GRISE-UPM.
2. Crea el fork en tu cuenta.
3. Abre el fork en tu copia local.
4. Sustituye el contenido del fichero README.md por esta única línea:

```text
Marco Adrian Padilla Triviño
```

5. Guarda los cambios y crea un commit con un mensaje claro, por ejemplo: "Añade README con mi nombre".

## Usuario

Perfecto, ya hice esa parte.

## Gemini

Entonces la primera entrega queda cerrada. El README debe quedar exactamente con tu nombre y sin texto adicional.

---

## Paso 2. Preparar las pruebas unitarias

## Usuario

Ahora necesito hacer las pruebas de unidad para la clase File. ¿Qué debería comprobar exactamente?

## Gemini

Debes cubrir constructor, addProperty, addImageBytes y removeContent. La idea es comprobar tanto casos válidos como errores esperados.

## Usuario

¿Me lo puedes resumir de forma más concreta?

## Gemini

Claro. Lo importante es esto:

- El constructor debe dejar el contenido vacío, sin ser null, y guardar type y location.
- addProperty debe aceptar textos del tipo clave=valor y lanzar excepción si el contenido es null, si el fichero es de tipo IMAGE o si el formato no es válido.
- addImageBytes debe aceptar bytes válidos, rechazar null, rechazar archivos de tipo PROPERTY y lanzar error si algún carácter supera 255.
- removeContent debe borrar los últimos caracteres y, si pides borrar más de los que hay, dejarlo vacío sin fallar.

## Usuario

Bien. ¿Cómo organizarías los tests?

## Gemini

Los organizaría por método, con nombres claros y casos separados. Por ejemplo:

- pruebas para el constructor
- pruebas para addProperty
- pruebas para addImageBytes
- pruebas para removeContent

Así se entiende rápido qué valida cada test y es fácil mantenerlos.

## Usuario

¿Me puedes dar el código de los tests?

## Gemini

Sí. Te dejo una propuesta completa en JUnit 5 para cubrir todos los casos principales:

```java
package es.upm.grise.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import es.upm.grise.file.exceptions.InvalidContentException;
import es.upm.grise.file.exceptions.WrongEncodingException;
import es.upm.grise.file.exceptions.WrongFileTypeException;

public class FileTest {

  @Test
  void testConstructor_ContentIsEmptyNotNull() {
    File file = new File(FileType.PROPERTY, new Location("/tmp", "test.txt"));
    assertTrue(file.getContent().isEmpty());
  }

  @Test
  void testConstructor_TypeIsAssigned() {
    File file = new File(FileType.IMAGE, new Location("/tmp", "test.png"));
    assertEquals(FileType.IMAGE, file.getType());
  }

  @Test
  void testConstructor_LocationIsAssigned() {
    Location location = new Location("/tmp", "test.txt");
    File file = new File(FileType.PROPERTY, location);
    assertEquals(location, file.getLocation());
  }

  @Test
  void testAddProperty_ValidKeyValue_AddsContent() {
    File file = new File(FileType.PROPERTY, new Location("/tmp", "test.txt"));
    assertDoesNotThrow(() -> file.addProperty("name=value".toCharArray()));
  }

  @Test
  void testAddProperty_AppendsToExistingContent() {
    File file = new File(FileType.PROPERTY, new Location("/tmp", "test.txt"));
    file.addProperty("name=value".toCharArray());
    file.addProperty("age=20".toCharArray());
    assertEquals(2, file.getContent().size());
  }

  @Test
  void testAddProperty_NullContent_ThrowsInvalidContentException() {
    File file = new File(FileType.PROPERTY, new Location("/tmp", "test.txt"));
    assertThrows(InvalidContentException.class, () -> file.addProperty(null));
  }

  @Test
  void testAddProperty_ImageType_ThrowsWrongFileTypeException() {
    File file = new File(FileType.IMAGE, new Location("/tmp", "test.png"));
    assertThrows(WrongFileTypeException.class, () -> file.addProperty("name=value".toCharArray()));
  }

  @Test
  void testAddProperty_EmptyContent_ThrowsInvalidContentException() {
    File file = new File(FileType.PROPERTY, new Location("/tmp", "test.txt"));
    assertThrows(InvalidContentException.class, () -> file.addProperty("".toCharArray()));
  }

  @Test
  void testAddProperty_NoEqualsSign_ThrowsInvalidContentException() {
    File file = new File(FileType.PROPERTY, new Location("/tmp", "test.txt"));
    assertThrows(InvalidContentException.class, () -> file.addProperty("invalid".toCharArray()));
  }

  @Test
  void testAddProperty_MultipleEqualsSigns_ThrowsInvalidContentException() {
    File file = new File(FileType.PROPERTY, new Location("/tmp", "test.txt"));
    assertThrows(InvalidContentException.class, () -> file.addProperty("a=b=c".toCharArray()));
  }

  @Test
  void testAddProperty_EqualsAtStart_ThrowsInvalidContentException() {
    File file = new File(FileType.PROPERTY, new Location("/tmp", "test.txt"));
    assertThrows(InvalidContentException.class, () -> file.addProperty("=value".toCharArray()));
  }

  @Test
  void testAddProperty_EqualsAtEnd_ThrowsInvalidContentException() {
    File file = new File(FileType.PROPERTY, new Location("/tmp", "test.txt"));
    assertThrows(InvalidContentException.class, () -> file.addProperty("name=".toCharArray()));
  }

  @Test
  void testAddImageBytes_ValidContent_AddsContent() {
    File file = new File(FileType.IMAGE, new Location("/tmp", "test.png"));
    assertDoesNotThrow(() -> file.addImageBytes(new char[] { 'a', 'b', 'c' }));
  }

  @Test
  void testAddImageBytes_AppendsToExistingContent() {
    File file = new File(FileType.IMAGE, new Location("/tmp", "test.png"));
    file.addImageBytes(new char[] { 'a', 'b' });
    file.addImageBytes(new char[] { 'c' });
    assertEquals(3, file.getContent().size());
  }

  @Test
  void testAddImageBytes_NullContent_ThrowsInvalidContentException() {
    File file = new File(FileType.IMAGE, new Location("/tmp", "test.png"));
    assertThrows(InvalidContentException.class, () -> file.addImageBytes(null));
  }

  @Test
  void testAddImageBytes_PropertyType_ThrowsWrongFileTypeException() {
    File file = new File(FileType.PROPERTY, new Location("/tmp", "test.txt"));
    assertThrows(WrongFileTypeException.class, () -> file.addImageBytes(new char[] { 'a' }));
  }

  @Test
  void testAddImageBytes_CharGreaterThan255_ThrowsWrongEncodingException() {
    File file = new File(FileType.IMAGE, new Location("/tmp", "test.png"));
    assertThrows(WrongEncodingException.class, () -> file.addImageBytes(new char[] { (char) 256 }));
  }

  @Test
  void testAddImageBytes_CharAt255_IsValidBoundary() {
    File file = new File(FileType.IMAGE, new Location("/tmp", "test.png"));
    assertDoesNotThrow(() -> file.addImageBytes(new char[] { (char) 255 }));
  }

  @Test
  void testRemoveContent_RemovesLastNChars() {
    File file = new File(FileType.IMAGE, new Location("/tmp", "test.png"));
    file.addImageBytes(new char[] { 'a', 'b', 'c' });
    file.removeContent(1);
    assertEquals(2, file.getContent().size());
  }

  @Test
  void testRemoveContent_ZeroChars_LeavesContentUnchanged() {
    File file = new File(FileType.IMAGE, new Location("/tmp", "test.png"));
    file.addImageBytes(new char[] { 'a', 'b' });
    file.removeContent(0);
    assertEquals(2, file.getContent().size());
  }

  @Test
  void testRemoveContent_NumberCharsEqualsSize_ClearsContent() {
    File file = new File(FileType.IMAGE, new Location("/tmp", "test.png"));
    file.addImageBytes(new char[] { 'a', 'b' });
    file.removeContent(2);
    assertTrue(file.getContent().isEmpty());
  }

  @Test
  void testRemoveContent_NumberCharsGreaterThanSize_ClearsContentWithoutException() {
    File file = new File(FileType.IMAGE, new Location("/tmp", "test.png"));
    file.addImageBytes(new char[] { 'a', 'b' });
    assertDoesNotThrow(() -> file.removeContent(10));
    assertTrue(file.getContent().isEmpty());
  }

  @Test
  void testRemoveContent_OnEmptyContent_NoExceptionAndStaysEmpty() {
    File file = new File(FileType.IMAGE, new Location("/tmp", "test.png"));
    assertDoesNotThrow(() -> file.removeContent(1));
    assertTrue(file.getContent().isEmpty());
  }
}
```

## Usuario

¿Tengo que tocar algo más para que funcione?

## Gemini

Sí. Revisa que el proyecto tenga la dependencia de JUnit 5 para poder ejecutar los tests correctamente. Si todo está bien, al final deberías ver que pasan todas las pruebas.

## Usuario

Perfecto, ya quedó.

## Gemini

Entonces la práctica queda documentada como un trabajo guiado paso a paso conmigo, con las pruebas y el archivo de conversación listos para entregar.
