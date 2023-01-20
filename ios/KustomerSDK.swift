//
 //  KustomerSDK.swift
 import KustomerChat

 @objc(KustomerSDK)
 class KustomerSDK: NSObject {

  @objc func identify(_ hash: NSString, resolver resolve: @escaping RCTPromiseResolveBlock,
                          rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    if(Kustomer.chatProvider.currentCustomer() == nil) {
      Kustomer.logIn(jwt: hash as String) { result in
        switch result {
          case .success:
            resolve(true)
          case .failure(let error):
            print(error.localizedDescription)
            reject("error",error.localizedDescription,error)
        }
      }
    }
  }

  @objc(presentSupport)
  func presentSupport() -> Void {
    DispatchQueue.main.async {
     Kustomer.show()
    }
  }

  @objc(openConversationsCount)
  func openConversationsCount() -> Int {
     return Kustomer.openConversationCount()
  }

  @objc(resetTracking)
  func resetTracking() -> Void {
    DispatchQueue.main.async {
     Kustomer.logOut({ error in
         if error != nil {
           print("there was a problem \(error?.localizedDescription ?? "")")
         }
       })
    }
  }

  @objc(describeCustomer:)
  func describeCustomer(data: [AnyHashable : Any]) -> Void {
   var emails = [String]()
   let email = data["email"] as? String
   if data["email"] != nil && email?.count != 0 {
    emails.append(email!)
   }
   var phones = [String]()
   let phone = data["phone"] as? String
   if data["phone"] != nil && phone?.count != 0 {
    phones.append(phone!)
   }
   var customs = [String : Any]()
   let custom = data["custom"] as? [String: Any]
   if custom != nil {
    customs = custom!
   }
   Kustomer.chatProvider.describeCurrentCustomer(phones: phones, emails: emails, custom: customs) { result in
    switch result {
     case .success:
      print("ok")
     case .failure(let error):
      print(error)
    }
   }
  }
     
 }
