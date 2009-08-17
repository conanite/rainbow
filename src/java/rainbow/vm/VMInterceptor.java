package rainbow.vm;

public interface VMInterceptor {
  VMInterceptor NULL = new NullInterceptor();
  VMInterceptor DEBUG = new DebugInterceptor();
  VMInterceptor KILL = new KillInterceptor();

  void check(VM vm);
  void end(VM vm);
}
