package qa.dboyko.rococo.extensions;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import qa.dboyko.rococo.api.core.ThreadSafeCookieFilter;

@NonNullByDefault
public class CookieStoreExtension implements AfterTestExecutionCallback {
  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
      ThreadSafeCookieFilter.INSTANCE.get().getCookieStore().clear();
  }
}
