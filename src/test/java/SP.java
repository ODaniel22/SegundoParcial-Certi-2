import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class SP
{

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito"); // 🕵️‍♂️ Modo incógnito
        options.addArguments("--disable-save-password-bubble"); // Evita sugerencias de contraseña
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false
        ));

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://www.saucedemo.com/");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
    }
    @Test
    public void Carrito() throws InterruptedException {


    WebElement Add_to_Cart_SLB = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-sauce-labs-backpack")));
    Add_to_Cart_SLB.click();

    WebElement Add_to_Cart_SLBL = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-sauce-labs-bike-light")));
    Add_to_Cart_SLBL.click();

    // Assertion opcional: validar que el carrito tiene 2 productos
    WebElement cartBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("shopping_cart_badge")));
    Assertions.assertEquals("2", cartBadge.getText(), "Cantidad incorrecta en el carrito");
    Thread.sleep(2500);
}

    @Test
    public void Checkout_con_Productos_sin_datos() throws InterruptedException {
        Carrito();

        driver.findElement(By.id("shopping_cart_container")).click();

        List<WebElement> productosEnCarrito = driver.findElements(By.cssSelector(".cart_item"));
        Assertions.assertFalse(productosEnCarrito.isEmpty(), "El carrito está vacío, no se encontraron productos");

        for (WebElement producto : productosEnCarrito) {
            WebElement cantidad = producto.findElement(By.cssSelector(".cart_quantity"));
            WebElement descripcion = producto.findElement(By.cssSelector(".inventory_item_name"));

            System.out.println("Producto: " + descripcion.getText() + " | Cantidad: " + cantidad.getText());
            Assertions.assertTrue(Integer.parseInt(cantidad.getText()) > 0, "Cantidad inválida para el producto");
        }
        Thread.sleep(2500);
        driver.findElement(By.id("checkout")).click();
        Thread.sleep(2500);
        WebElement buttomContinue = driver.findElement(By.id("continue"));
        buttomContinue.click();
        //Guardo la URL actual
        String urlActual = driver.getCurrentUrl();
        Assertions.assertTrue(urlActual.contains("checkout-step-one.html"), "Se redirigió sin datos válidos");

    }

    @Test
    public void Checkout_Sin_productos() throws InterruptedException {
        Thread.sleep(2500);
        driver.findElement(By.id("shopping_cart_container")).click();
        Thread.sleep(2500);
        List<WebElement> productosEnCarrito = driver.findElements(By.cssSelector(".cart_item"));
        Assertions.assertTrue(productosEnCarrito.isEmpty(), "El carrito está lleno, se encontraron productos");
        WebElement buttomCheckout = driver.findElement(By.id("checkout"));
        buttomCheckout.click();
        Thread.sleep(2500);
        String urlActual = driver.getCurrentUrl();
        Assertions.assertTrue(urlActual.contains("cart.html"), "Se redirigió sin productos");
    }

    @Test
    public void Checkout_Con_productos() throws InterruptedException {
        Carrito();

        driver.findElement(By.id("shopping_cart_container")).click();

        List<WebElement> productosEnCarrito = driver.findElements(By.cssSelector(".cart_item"));
        Assertions.assertFalse(productosEnCarrito.isEmpty(), "El carrito está vacío, no se encontraron productos");

        for (WebElement producto : productosEnCarrito) {
            WebElement cantidad = producto.findElement(By.cssSelector(".cart_quantity"));
            WebElement descripcion = producto.findElement(By.cssSelector(".inventory_item_name"));

            System.out.println("Producto: " + descripcion.getText() + " | Cantidad: " + cantidad.getText());
            Assertions.assertTrue(Integer.parseInt(cantidad.getText()) > 0, "Cantidad inválida para el producto");
        }

        driver.findElement(By.id("checkout")).click();

        driver.findElement(By.id("first-name")).sendKeys("Oscar Daniel");
        driver.findElement(By.id("last-name")).sendKeys("Vargas Sirpa");
        driver.findElement(By.id("postal-code")).sendKeys("1569");
        Thread.sleep(2500);
        driver.findElement(By.id("continue")).click();
        Thread.sleep(2500);
        WebElement ButtomFinish = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("finish")));
        ButtomFinish.click();
        WebElement mensajeConfirmacion = driver.findElement(By.cssSelector(".complete-header"));
        Assertions.assertEquals("Thank you for your order!", mensajeConfirmacion.getText());
    }

    @Test
    public void Reset_App_State() throws InterruptedException {

        Carrito();

        WebElement Menu = driver.findElement(By.id("react-burger-menu-btn"));
        Menu.click();

        WebElement Reset_App_State = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("reset_sidebar_link")));
        Reset_App_State.click();

        List<WebElement> cartBadges = driver.findElements(By.cssSelector(".shopping_cart_badge"));
        Assertions.assertTrue(cartBadges.isEmpty(), "El carrito debería estar vacío, pero el badge aún existe");


        // Verificar que el botón "Remove" ya no está presente
        boolean removeBtnVisibleB = driver.findElements(By.id("remove-sauce-labs-backpack")).size() > 0;
        Assertions.assertFalse(removeBtnVisibleB, "El botón 'Remove' aún está presente tras el reset mochila");
        // Verificar que el botón "Remove" ya no está presente
        boolean removeBtnVisibleBL = driver.findElements(By.id("remove-sauce-labs-bike-light")).size() > 0;
        Assertions.assertFalse(removeBtnVisibleBL, "El botón 'Remove' aún está presente tras el reset bicicleta");

    }

}
