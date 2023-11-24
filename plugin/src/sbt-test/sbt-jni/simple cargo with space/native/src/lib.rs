// This is the interface to the JVM that we'll call the majority of our
// methods on.
use jni::JNIEnv;

// These objects are what you should use as arguments to your native
// function. They carry extra lifetime information to prevent them escaping
// this context and getting used after being GC'd.
use jni::objects::JObject;

use jni::sys::jint;

// This keeps Rust from "mangling" the name and making it unique for this
// crate.
#[no_mangle]
pub extern "system" fn Java_simplecargo_Adder_plus(
  env: JNIEnv,
  object: JObject,
  term: jint,
) -> jint {
  let base = env.get_field(object, "base", "I").unwrap().i().unwrap();
  println!("Printing from rust library. base: {}", base);
  println!("Printing from rust library. term: {}", term);
  base + term
}
