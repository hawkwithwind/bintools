void __fastcall didAddDyldImage_104B8768C(void *aMachOHeader)
{
  void *machoHeader; // x26
  signed int v2_controlflow; // w8
  signed int v3_controlflow; // w8
  bool v4; // zf
  signed int v5_controlflow; // w8
  signed int v6_controlflow; // w8
  unsigned int v7_controlflow; // w8
  signed int v8_controlflow; // w8
  bool v9; // nf
  unsigned __int8 v10; // vf
  signed int v11_controlflow; // w8
  unsigned __int64 v12; // x20
  void *v13; // x0
  void **v14; // [xsp+0h] [xbp-F0h]
  char *v15; // [xsp+8h] [xbp-E8h]
  void **v16; // [xsp+10h] [xbp-E0h]
  char *v17; // [xsp+18h] [xbp-D8h]
  signed int main_controlflow; // [xsp+24h] [xbp-CCh]
  void *v19; // [xsp+28h] [xbp-C8h]
  unsigned __int64 v20; // [xsp+30h] [xbp-C0h]
  unsigned __int64 v21; // [xsp+38h] [xbp-B8h]
  Dl_info dlInfo; // [xsp+40h] [xbp-B0h]
  unsigned __int8 v23; // [xsp+66h] [xbp-8Ah]
  bool v24; // [xsp+67h] [xbp-89h]
  Dl_info *v25; // [xsp+68h] [xbp-88h]
  Dl_info *v26; // [xsp+70h] [xbp-80h]
  __int64 v27; // [xsp+78h] [xbp-78h]
  void **v28; // [xsp+80h] [xbp-70h]
  const char *v29; // [xsp+88h] [xbp-68h]
  unsigned __int64 v30; // [xsp+90h] [xbp-60h]

  machoHeader = aMachOHeader;
  v23 = (std::mutexP::lock_qword_1075484D0 != 0) ^ (qword_10853C4C8 != 0);
  v24 = __PAIR__(std::mutexP::lock_qword_1075484D0, qword_10853C4C8) == 0;
  main_controlflow = 0x466DECAC;
  do
  {
    while ( 1 )
    {
      while ( 1 )
      {
        while ( 1 )
        {
          while ( main_controlflow <= 0xD0FB622 )
          {
            if ( main_controlflow <= (signed int)0xD99B9EC7 )
            {
              switch ( main_controlflow )
              {
                case (int)0x83870423:
                  std::__1::mutex::unlock((std::__1::mutex *)std::mutexP::lock_qword_1075484D0);
                  main_controlflow = 0x5126A64E;
                  break;
                case (int)0x86797E14:
                  memcpy(v16, v29, v30);
                  main_controlflow = 0x95ACD590;
                  v14 = v16;
                  v15 = v17;
                  break;
                case (int)0x95ACD590:
                  *((_BYTE *)v14 + v30) = 0;
                  sub_10100F444(v27, (__int64)&v19, (__int128 *)&v19);
                  if ( *v15 >= 0 )
                    v6_controlflow = 0x83870423;
                  else
                    v6_controlflow = 0x3A63265;
                  main_controlflow = v6_controlflow;
                  break;
              }
            }
            else if ( main_controlflow > (signed int)0xF657A13A )
            {
              if ( main_controlflow == 0xF657A13B )
              {
                v12 = (v30 + 16) & 0xFFFFFFFFFFFFFFF0LL;
                v13 = (void *)operator new(v12);
                v21 = v12 | 0x8000000000000000LL;
                v19 = v13;
                v20 = v30;
                main_controlflow = 0x86797E14;
                v16 = (void **)v13;
                v17 = (char *)&v21 + 7;
              }
              else if ( main_controlflow == 0x3A63265 )
              {
                operator delete(v19);
                main_controlflow = 0x83870423;
              }
            }
            else
            {
              if ( main_controlflow == 0xD99B9EC8 )
                return;
              if ( main_controlflow == 0xDA626E1F )
              {
                if ( v30 >= 0x17 )
                  v3_controlflow = 0xF657A13B;
                else
                  v3_controlflow = 0x2D72E135;
                main_controlflow = v3_controlflow;
              }
            }
          }
          if ( main_controlflow > 0x466DECAB )
            break;
          if ( main_controlflow > 0x2C8A65E5 )
          {
            if ( main_controlflow == 0x2C8A65E6 )
            {
              main_controlflow = 0x2C8A65E6;
            }
            else if ( main_controlflow == 0x2D72E135 )
            {
              HIBYTE(v21) = v30;
              if ( v30 )
                v7_controlflow = 0x86797E14;
              else
                v7_controlflow = 0x95ACD590;
              main_controlflow = v7_controlflow;
              v16 = v28;
              v17 = (char *)&v21 + 7;
              v14 = v28;
              v15 = (char *)&v21 + 7;
            }
          }
          else if ( main_controlflow == 0xD0FB623 )
          {
            v25 = &dlInfo;
            v4 = dladdr(machoHeader, &dlInfo) == 0;
            v5_controlflow = 0x6FE4EE37;
                                    LABEL_52:
            if ( v4 )
              v5_controlflow = 0x5126A64E;
            main_controlflow = v5_controlflow;
          }
          else if ( main_controlflow == 0x222DA1E3 )
          {
            std::__1::mutex::lock((std::__1::mutex *)std::mutexP::lock_qword_1075484D0);
            v27 = qword_10853C4C8;
            v28 = &v19;
            v29 = v26->dli_fname;
            v20 = 0LL;
            v21 = 0LL;
            v19 = 0LL;
            v30 = strlen(v29);
            if ( v30 <= 0xFFFFFFFFFFFFFFEFLL )
              v2_controlflow = 0xDA626E1F;
            else
              v2_controlflow = 0x4AEB346F;
            main_controlflow = v2_controlflow;
          }
        }
        if ( main_controlflow <= 0x5126A64D )
          break;
        if ( main_controlflow == 0x5126A64E )
        {
          if ( (dword_108782324 - 1) * dword_108782324 & 1 )
          {
            v10 = __OFSUB__(dword_108782354, 10);
            v9 = dword_108782354 - 10 < 0;
          }
          else
          {
            v10 = 0;
            v9 = 1;
          }
          if ( v9 ^ v10 )
            v11_controlflow = 0xD99B9EC8;
          else
            v11_controlflow = 0x2C8A65E6;
          main_controlflow = v11_controlflow;
        }
        else if ( main_controlflow == 0x6FE4EE37 )
        {
          v26 = &dlInfo;
          v4 = dlInfo.dli_fname == 0LL;
          v5_controlflow = 0x222DA1E3;
          goto LABEL_52;
        }
      }
      if ( main_controlflow != 0x466DECAC )
        break;
      if ( (v23 | v24) & 1 )
        v8_controlflow = 0xD99B9EC8;
      else
        v8_controlflow = 0xD0FB623;
      main_controlflow = v8_controlflow;
    }
  }
  while ( main_controlflow != 0x4AEB346F );
  std::__1::__basic_string_common<true>::__throw_length_error(&v19);
}