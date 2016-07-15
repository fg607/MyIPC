// ISecurityCenter.aidl
package com.hardwork.fg607.myipc.binderpool;


interface ISecurityCenter {

   String encrypt(String content);
   String decrypt(String password);
}
