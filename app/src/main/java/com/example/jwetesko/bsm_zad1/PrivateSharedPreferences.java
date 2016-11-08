package com.example.jwetesko.bsm_zad1;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by jwetesko on 03.11.16.
 */

public class PrivateSharedPreferences implements SharedPreferences {
    private static final String UTF8 = "utf-8";

    private SharedPreferences delegate;
    private Context context;
    public String enteredPassword;

    public PrivateSharedPreferences(Context context, SharedPreferences delegate, String enteredPassword) {
        this.delegate = delegate;
        this.context = context;
        this.enteredPassword = enteredPassword;
    }

    public class Editor implements SharedPreferences.Editor {
        protected SharedPreferences.Editor delegate;
        public Editor() {
            this.delegate = PrivateSharedPreferences.this.delegate.edit();
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            delegate.putString(key, encrypt(Boolean.toString(value)));
            return this;
        }

        @Override
        public Editor putFloat(String key, float value) {
            delegate.putString(key, encrypt(Float.toString(value)));
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            delegate.putString(key, encrypt(Integer.toString(value)));
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            delegate.putString(key, encrypt(Long.toString(value)));
            return this;
        }

        @Override
        public Editor putString(String key, String value) {
            delegate.putString(key, encrypt(value));
            return this;
        }

        @Override
        public Editor putStringSet(String key, Set<String> value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void apply() {
            delegate.apply();
        }

        @Override
        public Editor clear() {
            delegate.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return delegate.commit();
        }

        @Override
        public Editor remove(String s) {
            delegate.remove(s);
            return this;
        }
    }

    public Editor edit() {
        return new Editor();
    }

    @Override
    public Map<String, ?> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        final String v = delegate.getString(key, null);
        return v!=null ? Boolean.parseBoolean(decrypt(v)) : defValue;
    }

    @Override
    public float getFloat(String key, float defValue) {
        final String v = delegate.getString(key, null);
        return v!=null ? Float.parseFloat(decrypt(v)) : defValue;
    }

    @Override
    public int getInt(String key, int defValue) {
        final String v = delegate.getString(key, null);
        return v!=null ? Integer.parseInt(decrypt(v)) : defValue;
    }

    @Override
    public long getLong(String key, long defValue) {
        final String v = delegate.getString(key, null);
        return v!=null ? Long.parseLong(decrypt(v)) : defValue;
    }

    @Override
    public String getString(String key, String defValue) {
        final String v = delegate.getString(key, null);
        return v != null ? decrypt(v) : defValue;
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(String s) {
        enteredPassword = s;
        return true;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        delegate.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        delegate.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    private String encrypt(String value) {

        try {
            final byte[] bytes = value!=null ? value.getBytes(UTF8) : new byte[0];

            final Random r = new SecureRandom();
            byte[] salt = new byte[15];
            r.nextBytes(salt);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(enteredPassword.toCharArray(), salt, 1000, 256);

            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = cipher.getParameters();
            byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            byte[] ciphertext = cipher.doFinal(bytes);

            String result = new String(Base64.encode(ciphertext, Base64.NO_WRAP),UTF8) + ", " + new String(Base64.encode(iv, Base64.NO_WRAP),UTF8) + ", " + new String(Base64.encode(salt, Base64.NO_WRAP),UTF8);
            System.out.println(result);

            return result;

        } catch( Exception e ) {
            throw new RuntimeException(e);
        }

    }

    private String decrypt(String value){
        try {
            List<String> codesList = Arrays.asList(value.split(","));

            String ciphertextString = codesList.get(0);
            String ivString = codesList.get(1);
            String saltString = codesList.get(2);

            final byte[] ciphertext = Base64.decode(ciphertextString, Base64.DEFAULT);
            final byte[] iv = Base64.decode(ivString, Base64.DEFAULT);
            final byte[] salt = Base64.decode(saltString, Base64.DEFAULT);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(enteredPassword.toCharArray(), salt, 1000, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            return new String(cipher.doFinal(ciphertext), "UTF-8");

        } catch( Exception e) {
            throw new RuntimeException(e);
        }
    }

}
