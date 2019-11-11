//
// set target func ptr by type:
//
// type = aType/8
//
// result_cpp_str_ptr[0] = WCS::CAPT::getGlobalInstance;
// result_cpp_str_ptr[1] = (__int64 (*)())WCS::CAPT_setGlobalInstance;
// result_cpp_str_ptr[2] = (__int64 (*)())sub_103B66230;
// result_cpp_str_ptr[3] = (__int64 (*)())sub_103B6050C;
// result_cpp_str_ptr[4] = (__int64 (*)())sub_103B660C0;
// result_cpp_str_ptr[5] = (__int64 (*)())sub_103B66178;
// result_cpp_str_ptr[6] = (__int64 (*)())sub_103B5EF6C;
// result_cpp_str_ptr[7] = (__int64 (*)())sub_103B5EF70;
//
void __fastcall xxx_getFunc_by_type_104B17E94(__int64 aResult_cpp_str_ptr, __int64 aType)
{
  __int64 (**result_cpp_str_ptr)(); // x19
  bool v3; // nf
  unsigned __int8 v4; // vf
  signed int v5_controlflow; // w8
  signed int v6; // w9
  bool v7; // zf
  signed int v8_controlflow; // w8
  signed int v9; // w9
  bool v10; // nf
  unsigned __int8 v11; // vf
  unsigned int v12_controlflow; // w8
  signed int v13; // w9
  signed int v14_controlflow; // w8
  bool v15; // nf
  unsigned __int8 v16; // vf
  unsigned int v17_controlflow; // w8
  unsigned int v18_controlflow; // w8
  bool v19; // nf
  unsigned __int8 v20; // vf
  signed int v21_controlflow; // w8
  __int64 stack_var_type; // [xsp+0h] [xbp-70h]
  __int64 main_controlflow; // [xsp+8h] [xbp-68h]
  __int64 stack_var_xsp+0x10; // [xsp+10h] [xbp-60h]
  __int64 (**stack_var_xsp+0x18)(); // [xsp+18h] [xbp-58h]

  result_cpp_str_ptr = (__int64 (**)())aResult_cpp_str_ptr;
  HIDWORD(main_controlflow) = 0xA72C0581;
  stack_var_type = __ROR8__(aType, 3);          // v22 = aXXXlen / 8,
  do
  {
    while ( 1 )
    {
      while ( 1 )
      {
        while ( 1 )
        {
          while ( 1 )
          {
            while ( SHIDWORD(main_controlflow) > (signed int)0xCEB74811 )
            {
              if ( SHIDWORD(main_controlflow) > 0x2E970573 )
              {
                if ( SHIDWORD(main_controlflow) <= 0x3C0333E7 )
                {
                  switch ( HIDWORD(main_controlflow) )
                  {
                    case 0x2E970574:
                      v4 = __OFSUB__(stack_var_xsp+0x10, 5LL);
                      v3 = stack_var_xsp+0x10 - 5 < 0;
                      v5_controlflow = 0xD2A2A76D;
                      v6 = 0x5D9FA57E;
                      goto LABEL_92;
                    case 0x37319445:
                      stack_var_xsp+0x18 = result_cpp_str_ptr;
                                              LABEL_96:
                      *stack_var_xsp+0x18 = WCS::CAPT::getGlobalInstance;
                      if ( (dword_108782370 - 1) * dword_108782370 & 1 )
                      {
                        v20 = __OFSUB__(dword_108782388, 10);
                        v19 = dword_108782388 - 10 < 0;
                      }
                      else
                      {
                        v20 = 0;
                        v19 = 1;
                      }
                      if ( v19 ^ v20 )
                        v21_controlflow = 0xBDD7BB79;
                      else
                        v21_controlflow = 0x1CACBF1A;
                      HIDWORD(main_controlflow) = v21_controlflow;
                      break;
                    case 0x39E6EFCE:
                      v11 = __OFSUB__(stack_var_xsp+0x10, 3LL);
                      v10 = stack_var_xsp+0x10 - 3 < 0;
                      v12_controlflow = 0x9625F592;
                      v13 = 0x6C022D5A;
                      goto LABEL_82;
                  }
                }
                else if ( SHIDWORD(main_controlflow) > 0x5D9FA57D )
                {
                  if ( HIDWORD(main_controlflow) == 0x5D9FA57E )
                  {
                    result_cpp_str_ptr[4] = (__int64 (*)())sub_104B5A0C0;
                    HIDWORD(main_controlflow) = 0xBDD7BB79;
                  }
                  else if ( HIDWORD(main_controlflow) == 0x6C022D5A )
                  {
                    result_cpp_str_ptr[3] = (__int64 (*)())sub_104B5450C;
                    HIDWORD(main_controlflow) = 0xBDD7BB79;
                  }
                }
                else if ( HIDWORD(main_controlflow) == 0x3C0333E8 )
                {
                  if ( (dword_108782370 - 1) * dword_108782370 & 1 )
                  {
                    v16 = __OFSUB__(dword_108782388, 10);
                    v15 = dword_108782388 - 10 < 0;
                  }
                  else
                  {
                    v16 = 0;
                    v15 = 1;
                  }
                  if ( v15 ^ v16 )
                    v17_controlflow = 0x8E653F23;
                  else
                    v17_controlflow = 0xF27E0F4D;
                  HIDWORD(main_controlflow) = v17_controlflow;
                }
                else if ( HIDWORD(main_controlflow) == 0x4C578F32 )
                {
                  HIDWORD(main_controlflow) = 0xCEB74812;
                }
              }
              else if ( SHIDWORD(main_controlflow) <= (signed int)0xF27E0F4C )
              {
                switch ( HIDWORD(main_controlflow) )
                {
                  case 0xCEB74812:
                    if ( (unsigned int)should_log() )
                      v18_controlflow = 0xB65FB001;
                    else
                      v18_controlflow = 0xC70FC95F;
                    HIDWORD(main_controlflow) = v18_controlflow;
                    break;
                  case 0xD2A2A76D:
                    result_cpp_str_ptr[5] = (__int64 (*)())sub_104B5A178;
                    HIDWORD(main_controlflow) = 0xBDD7BB79;
                    break;
                  case 0xD5AAE1D4:
                    v4 = __OFSUB__(stack_var_xsp+0x10, 6LL);
                    v3 = stack_var_xsp+0x10 - 6 < 0;
                    v5_controlflow = 0xA911EC87;
                    v6 = 0x2E970574;
                    goto LABEL_92;
                }
              }
              else if ( SHIDWORD(main_controlflow) > 0x1CACBF19 )
              {
                if ( HIDWORD(main_controlflow) == 0x1CACBF1A )
                  goto LABEL_96;
                if ( HIDWORD(main_controlflow) == 0x2B21DBF9 )
                {
                  v11 = __OFSUB__(stack_var_xsp+0x10, 4LL);
                  v10 = stack_var_xsp+0x10 - 4 < 0;
                  v12_controlflow = 0xCD64769B;
                  v13 = 0xD5AAE1D4;
                  goto LABEL_82;
                }
              }
              else if ( HIDWORD(main_controlflow) == 0xF27E0F4D )
              {
                HIDWORD(main_controlflow) = 0xF27E0F4D;
              }
              else if ( HIDWORD(main_controlflow) == 0xAD88DF3 )
              {
                v7 = stack_var_xsp+0x10 == 7;
                v8_controlflow = 0x4C578F32;
                v9 = 0xCA044401;
                goto LABEL_64;
              }
            }
            if ( SHIDWORD(main_controlflow) > (signed int)0xA911EC86 )
              break;
            if ( SHIDWORD(main_controlflow) <= (signed int)0x9625F591 )
            {
              switch ( HIDWORD(main_controlflow) )
              {
                case 0x8E653F23:
                  stack_var_xsp+0x10 = stack_var_type;
                  HIDWORD(main_controlflow) = 0x2B21DBF9;
                  break;
                case 0x9237D5C1:
                  xlog(aIiL76iKln78mii, 4LL, aRejBidbj, 63LL, "", "", stack_var_type, main_controlflow);
                  if ( (dword_108782370 - 1) * dword_108782370 & 1 )
                  {
                    v11 = __OFSUB__(dword_108782388, 10);
                    v10 = dword_108782388 - 10 < 0;
                  }
                  else
                  {
                    v11 = 0;
                    v10 = 1;
                  }
                  v12_controlflow = 0xC70FC95F;
                  v13 = 0xBA54DE19;
                                              LABEL_82:
                  if ( !(v10 ^ v11) )
                    v12_controlflow = v13;
                  HIDWORD(main_controlflow) = v12_controlflow;
                  break;
                case 0x94D1DC2F:
                  v4 = __OFSUB__(stack_var_xsp+0x10, 1LL);
                  v3 = stack_var_xsp+0x10 - 1 < 0;
                  v5_controlflow = 0x9AB5220D;
                  v6 = 0xA4D69D3A;
                  goto LABEL_92;
              }
            }
            else if ( SHIDWORD(main_controlflow) > (signed int)0xA4D69D39 )
            {
              if ( HIDWORD(main_controlflow) == 0xA4D69D3A )
              {
                v7 = stack_var_xsp+0x10 == 0;
                v8_controlflow = 0x4C578F32;
                v9 = 0x37319445;
                                              LABEL_64:
                if ( v7 )
                  v8_controlflow = v9;
                HIDWORD(main_controlflow) = v8_controlflow;
              }
              else if ( HIDWORD(main_controlflow) == 0xA72C0581 )
              {
                if ( result_cpp_str_ptr )
                  v14_controlflow = 1006842856;
                else
                  v14_controlflow = 0xBDD7BB79;
                HIDWORD(main_controlflow) = v14_controlflow;
              }
            }
            else if ( HIDWORD(main_controlflow) == 0x9625F592 )
            {
              result_cpp_str_ptr[2] = (__int64 (*)())sub_104B5A230;
              HIDWORD(main_controlflow) = 0xBDD7BB79;
            }
            else if ( HIDWORD(main_controlflow) == 0x9AB5220D )
            {
              result_cpp_str_ptr[1] = (__int64 (*)())WCS::CAPT_setGlobalInstance;
              HIDWORD(main_controlflow) = 0xBDD7BB79;
            }
          }
          if ( SHIDWORD(main_controlflow) <= (signed int)0xC70FC95E )
            break;
          if ( SHIDWORD(main_controlflow) <= (signed int)0xCD64769A )
          {
            if ( HIDWORD(main_controlflow) == 0xCA044401 )
            {
              result_cpp_str_ptr[7] = (__int64 (*)())sub_104B52F70;
              HIDWORD(main_controlflow) = 0xBDD7BB79;
            }
            else if ( HIDWORD(main_controlflow) == 0xC70FC95F )
            {
              abort();
            }
          }
          else
          {
            if ( HIDWORD(main_controlflow) == 0xCD64769B )
            {
              v11 = __OFSUB__(stack_var_xsp+0x10, 2LL);
              v10 = stack_var_xsp+0x10 - 2 < 0;
              v12_controlflow = 0x94D1DC2F;
              v13 = 0x39E6EFCE;
              goto LABEL_82;
            }
            if ( HIDWORD(main_controlflow) == 0xCDE0AD57 )
            {
              result_cpp_str_ptr[6] = (__int64 (*)())sub_104B52F6C;
              HIDWORD(main_controlflow) = 0xBDD7BB79;
            }
          }
        }
        if ( SHIDWORD(main_controlflow) > (signed int)0xBA54DE18 )
          break;
        if ( HIDWORD(main_controlflow) == 0xA911EC87 )
        {
          v4 = __OFSUB__(stack_var_xsp+0x10, 7LL);
          v3 = stack_var_xsp+0x10 - 7 < 0;
          v5_controlflow = 0xAD88DF3;
          v6 = 0xCDE0AD57;
          goto LABEL_92;
        }
        if ( HIDWORD(main_controlflow) == 0xB65FB001 )
        {
          if ( (dword_108782370 - 1) * dword_108782370 & 1 )
          {
            v4 = __OFSUB__(dword_108782388, 10);
            v3 = dword_108782388 - 10 < 0;
          }
          else
          {
            v4 = 0;
            v3 = 1;
          }
          v5_controlflow = 0xBA54DE19;
          v6 = 0x9237D5C1;
                                              LABEL_92:
          if ( v3 ^ v4 )
            v5_controlflow = v6;
          HIDWORD(main_controlflow) = v5_controlflow;
        }
      }
      if ( HIDWORD(main_controlflow) != 0xBA54DE19 )
        break;
      xlog(aIiL76iKln78mii, 4LL, aRejBidbj, 63LL, "", "", stack_var_type, main_controlflow);
      HIDWORD(main_controlflow) = 0x9237D5C1;
    }
  }
  while ( HIDWORD(main_controlflow) != 0xBDD7BB79 );
}